import groovy.text.SimpleTemplateEngine

includeTargets << grailsScript("_GrailsInit")



// Reading parameters from command line
input = args.split('\n')
packageName= ''
inputClassName = ''
inputClassNameInLowercase = ''

public void configureParameters()
{
    packageName= input[0]
    inputClassName= input[1]
    inputClassNameInLowercase=inputClassName.toString().toLowerCase();
    println(packageName)
    println(inputClassName)
    println(inputClassNameInLowercase)
    println(appLocation)
}

//appLocation__ = new File(getClass().protectionDomain.codeSource.location.path).parent
//appLocation_ = System.properties['base.dir']

appLocation = new File(".").getCanonicalPath()
//pluginName = 'DasProviderPlugin'
//def pluginDir = appLocation
templateDir = "$appLocation/src/templates"
appDir = "$appLocation/grails-app"
srcDir = "$appLocation/src"

templateEngine = new SimpleTemplateEngine()


target(main: "The description of the script goes here!") {

    configureParameters()
    createClassFromTemplate()
}

packageToDir = { String packageName ->
    String dir = ''
    if (packageName) {
        dir = packageName.replaceAll('\\.', '/') + '/'
    }
    return dir
}

overwriteAll = false
okToWrite = { String dest ->

    def file = new File(dest)
    if (overwriteAll || !file.exists()) {
        return true
    }

    String propertyName = "file.overwrite.$file.name"
    ant.input(addProperty: propertyName, message: "$dest exists, ok to overwrite?",
            validargs: 'y,n,a', defaultvalue: 'y')

    if (ant.antProject.properties."$propertyName" == 'n') {
        return false
    }

    if (ant.antProject.properties."$propertyName" == 'a') {
        overwriteAll = true
    }

    true
}

generateFile = { String templatePath, String outputPath ->
    if (!okToWrite(outputPath)) {
        return
    }

    File templateFile = new File(templatePath)
    if (!templateFile.exists()) {
        errorMessage "\nERROR: $templatePath doesn't exist"
        return
    }

    File outFile = new File(outputPath)

    // in case it's in a package, create dirs
    ant.mkdir dir: outFile.parentFile

    println "About to generate $inputClassName."
    outFile.withWriter { writer ->
        templateEngine.createTemplate(templateFile.text).make(templateAttributes).writeTo(writer)
    }

    printMessage "generated $outFile.absolutePath"
}



public void createClassFromTemplate()
{
    templateAttributes = [packageName: packageName,
                          inputClassName:inputClassName,
                          inputClassNameInLowercase:inputClassNameInLowercase]
    try{
        String dir = packageToDir(packageName)

        String destClass = "$srcDir/java/${dir}${inputClassName}.java"
        generateFile "$templateDir/classBuilder.tmpl", destClass
       printMessage "Class  created at ${destClass}."
    }catch(Exception e){
        print(e)
    }
}

printMessage = { String message -> event('StatusUpdate', [message]) }
errorMessage = { String message -> event('StatusError', [message]) }

setDefaultTarget(main)
