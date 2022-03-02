package com.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class CqwApplicationContext {

     //定义config配置类
    private  Class configClass;
    private ConcurrentHashMap<String ,Object> singletonObjects =
            new ConcurrentHashMap<>();//单例池
    //存放扫描到的所有bean的beandfinition对象，保存了所有bean的信息
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap=new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList =new ArrayList<>();

    //CopyOnWriteArraySet是线程安全的
    private Set<Class<?>> aopClassSet =new CopyOnWriteArraySet<>();





     public CqwApplicationContext(Class configClass){

         //总结思路：1.通过配置类获取上面的注解，通过注解获取想要扫描的类路径
//        2.使用application类加载器加载类 类路径下的文件
//         3.加载类文件到jvm中判断类上是否有component注解
         //4.如果有 创建beandefinition对象，将bean的信息加入definition中，最后一起加入definitionmap中，
         //5、如果是singleton对象，在扫描类结束后，直接创建加入singletonmap中
         //6.如果是prototype类型，则在getbean时创建bean对象，返回
        this.configClass=configClass;

        //解析配置类
         // ComponentScan注解-->扫描路径-->扫描
         //扫描：将所有的bean对象放到beandifinitionmap中
         scan(configClass);

         //对单例bean生成bean对象
         //从beanDefinitionmap对象中获取beandefinition对象，判断对象是否为singleton
         // ，然后创建对象，放入singletonmap中
         for(Map.Entry<String,BeanDefinition> entry:
                 beanDefinitionMap.entrySet()){
             BeanDefinition beanDefinition=entry.getValue();
             String beanName=entry.getKey();
             if(beanDefinition.getScope().equals("singleton")){
                 Object bean = createBean(beanName,beanDefinition);
                 singletonObjects.put(beanName,bean);
             }
         }



     }

     //创建bean对象  通过beandifitition中获取class对象，调用构造器创建对象-->依赖注入
     private Object createBean(String beanName,BeanDefinition beanDefinition){
         //通过beandefinition中的getClass方法获取clazz对象
         Class clazz = beanDefinition.getClazz();
         try {
             //调用class对象来调用无参构造方法生成对象
             Object instance = clazz.getDeclaredConstructor().newInstance();

             //依赖注入
             for(Field declaredField:clazz.getDeclaredFields()){

                 if(declaredField.isAnnotationPresent(Autowired.class)){
                     //直接byName了 直接从singletonMap中获取了 对于prototype对象直接创建
                     // ？？？（如果获取的时候singletonMap还没有呢？）
                     Object bean = getBean(declaredField.getName());
                     //如果单例池中没有并且required为true，则抛出异常
                     if(bean==null&&declaredField.getDeclaredAnnotation(Autowired.class).required()){
                         throw  new NullPointerException();
                     }
                     declaredField.setAccessible(true);
                     declaredField.set(instance,bean);
                 }
             }
             //Aware回调：对于实现BeanNameAware接口的类，将其beanName传递给对象
             if(instance instanceof BeanNameAware){
                 ((BeanNameAware)instance).setBeanName(beanName);
             }

             //在初始化前调用BeanPostProcessor，执行里面的代码
             //至于执行顺序，源码中有响应的功能可以自定义；因为不同文件系统的存储文件顺序可能不同，因此按照文件扫码顺序来判断beanpostProcessor的执行顺序是不可靠的
             for(BeanPostProcessor beanPostProcessor:beanPostProcessorList){

                 //返回前后放回后可能不是同一个值，因为不知道BeanPostProcessor内干了什么，有点像动态代理
                 instance= beanPostProcessor.postProcessBeforeInitialization(instance,
                         beanName);
             }

             //初始化：程序员自己定义的初始化，与 jvm的类初始化不同
             if(instance instanceof InitializingBean){
                 try {
                     ((InitializingBean)instance).afterPropertiesSet();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }

             //在初始化后调用BeanPostProcessor，执行里面的代码
             for(BeanPostProcessor beanPostProcessor:beanPostProcessorList){

                 instance=beanPostProcessor.postProcessAfterInitialization(instance,
                         beanName);
             }




             return instance;
         } catch (InstantiationException e) {
             e.printStackTrace();
         } catch (IllegalAccessException e) {
             e.printStackTrace();
         } catch (InvocationTargetException e) {
             e.printStackTrace();
         } catch (NoSuchMethodException e) {
             e.printStackTrace();
         }
         return null;

     }

     //执行AOP
     private  void doAOP (){

         if(aopClassSet.size()>0){
             for(Class<?> aop:aopClassSet){
                 Method[] declaredMethods = aop.getDeclaredMethods();
                 if(declaredMethods!=null){
                     for(Method method:declaredMethods){
                         if(method.isAnnotationPresent(Around.class)){

                             Around annotation = method.getAnnotation(Around.class);
                             //切入点表达式
                             String execution = annotation.execution();
                             //获取要代理类的全限列名
                             String fullName = execution.substring(0,
                                     execution.lastIndexOf("."));
                             //获取要执行的方法名
                             String methodName =
                                     execution.substring(execution.lastIndexOf(".") + 1);
                             try {
                                 Class<?> targetClass = Class.forName(fullName);
                                 //获取要代理类的目标对象，从ioc容器中获取,通过类名获取默认的名字
                                 String simpleName = targetClass.getSimpleName();
                                 String beanName=
                                         String.valueOf(simpleName.charAt(0)).toLowerCase()+simpleName.substring(1);
                                 Object bean=getBean(beanName);
                                 JdkProxy<Object> beanProxy =
                                         new JdkProxy<Object>(targetClass, methodName,
                                         bean, method, aop);

                                 //将bean代理类放回到容器中，判断如何是singleton的话放回去
                                 if(beanDefinitionMap.get(beanName).getScope()=="singleton"){
                                     singletonObjects.put(beanName,beanProxy);
                                 }



                             } catch (Exception e) {
                                 e.printStackTrace();
                             }

                         }


                     }
                 }

             }
         }

     }
    //扫描：将所有的bean对象放到beandifinitionmap中
    private void scan(Class configClass) {
        ComponentScan componentScanAnnotation =(ComponentScan)
                configClass.getDeclaredAnnotation(ComponentScan.class);
        String path=componentScanAnnotation.value();
        //转化path为路径
        path=path.replace(".","/");
        //通过扫描路径来找到类
        //使用application类加载器
        //获取类加载器
        ClassLoader classLoader = CqwApplicationContext.class.getClassLoader();
        //获取classpath路径下资源
        URL resource = classLoader.getResource(path);
        //resource其实是个目录，转成file对象，比较好用
        File file=new File(resource.getFile());
        if(file.isDirectory()){
            //获取目录下文件
            File[] files = file.listFiles();
            for(File f:files){
                //获取类的全限列名
                String fileName = f.getAbsolutePath();
                //判断文件是不是一个class文件
                if(fileName.endsWith(".class")){

                    String className =fileName.substring(fileName.indexOf("com")
                            ,fileName.indexOf(".class"));
                    className= className.replace("\\", ".");

                    try {
                        //使用类加载进行加载
                        Class<?> clazz = classLoader.loadClass(className);
                        //判断类上是否有componet注解
                        if(clazz.isAnnotationPresent(Component.class)){
                            
                            
                            //如果某个class继承了beanpostProccessor，那么生成它的对象加入list中
                            //在之后creatBean时调用
                            //isAssignableFrom 用于判断Class类是否被另一个Class类继承
                            if(BeanPostProcessor.class.isAssignableFrom(clazz)){
                                BeanPostProcessor instance =
                                        (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(instance);

                            }

                            //获取到实现aop的类,将其加入set中，直接continue，不加入Beandefinition中了
                            if(clazz.isAnnotationPresent(Aspect.class)){
                                aopClassSet.add(clazz);
                                continue;
                            }
                            

                            //表示这个类是一个bean
                            //解析类：判断当前bean是单例bean还是原型bean
                            //由Component注解的value值得到对象名字
                            //??? 如果component注解中没有value值，也就是默认值”“，怎么办
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();
                            //定义beandefinition对象
                            BeanDefinition beanDefinition=new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            //判断类上有没有scope注解
                            if(clazz.isAnnotationPresent(Scope.class)){
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            }
                            else{
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName,
                                    beanDefinition);

                        }
                    } catch (ClassNotFoundException | NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    //通过对象名字获取对象 ：singleton对象直接去singleton中获取，多例对象直接创建
    public Object getBean (String beanName){
         if(beanDefinitionMap.containsKey(beanName)){
             BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
             if(beanDefinition.getScope().equals("singleton")){
                 return singletonObjects.get(beanName);
             }
             else{
                 //创建Bean对象
                 //从beandifinitionmap中获取beandefinition对象，创建bean
                 Object bean = createBean(beanName,
                         beanDefinitionMap.get(beanName));
                 return  bean;

             }
         }
         else{
             System.out.println("不存在这个bean");
             throw new NullPointerException();
         }

    }
}
