**Examples available at : [libgdx-extension examples](https://bitbucket.org/KyuBlade/libgdx-extension-examples/)**
**Documentation available at : [libgdx-extension documentation](http://devblog.omega-project.com/libgdx-extension/)**

Currently, the project is not in a public Maven repository. Therefore, in order to obtain the library, you need to do the following:

**1 . Clone the project:**

```
#!git

git clone https://bitbucket.org/KyuBlade/libgdx-extension.git
```


**2 . Compile and install the library to your local repository. In the project root folder:**

Maven :
```
#!console

mvn clean install
```
Or Gradle :
```
#!console

gradle clean build install
```

**3 . If you are using maven in your project, add the maven dependency in your pom.xml**

```
#!maven

<dependency>
   <groupId>com.gdx.extension</groupId>
   <artifactId>libgdx-extension</artifactId>
   <version>1.2.0</version>
</dependency>
```

**Or when using Gradle add the following to your build.gradle:**


```
#!gradle

repositories {
   mavenLocal()
}

dependencies {
   compile "com.gdx.extension:libgdx-extension:1.2.0"
   compile "com.gdx.extension:libgdx-extension:1.2.0:sources"
}
```