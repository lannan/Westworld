The programs included here were created for the following paper:

"Westworld: Fuzzing-Assisted Remote Dynamic Symbolic Execution of Smart Apps on IoT Cloud Platforms"

Lannan Luo (University of South Carolina), Qiang Zeng (University of South Carolina), Bokai Yang (University of South Carolina), Fei Zuo (University of South Carolina), Junzhe Wang (University of South Carolina)


# remoteSE
How to run the symbolic tool:
1. import the project into eclipse or intellij. 
2. import all the jar files in the libary folder (the folder library is under the source folder).
3. download the newest chromedriver and put it in the source folder of Chrome (Of course you need to install Chrome first. There is a chromedriver.exe in the library folder. But I am 100% sure it is out of date and cannot be used any more).
4. import com.microsoft.z3.jar under the libary\z3-4.8.4.d6df51951f4c-x64-win\bin
5. Make sure the input paths are correct.
6. Run through the main method in Front.groovy.



How to run the fuzzing tool:

1. Import the whole project (using eclipse or intellij)
2. Import all the jar files in the folder library (the folder library is under the source folder)
3. Download the newest chromedriver.exe and put it in the source folder of Chrome (Of course you need to install Chrome first. There is a chromedriver.exe in the library folder. But I am 100% sure it is out of date and cannot be used any more)
4. Understand how to modify the inputs part and the selenium part (see below). Modify the files according to your need.
5. Make sure the input paths are correct.
6. Run the main method in Front.groovy.



*Below are some tips:



*How to modify the input part:

Use the 16Paths1.groovy as an example (under the fuzzing tool folder):

For the application (16Paths1.groovy) file:
Int the preferences part, there are serval sections. Each section has a variableName (After the keyword input. In this example,                     aa, bb, cc, dd are the variables names). And each of them has a TitleName (After the keyword title. In this example is integer ?）

Now we go back to our code:
In the file RunSystem.groovy (in line 74, 80, 86 and 92), we pass the variableName and the TitleName to the selenium part, So the selenium part can find the input box under the variableName and TitleName.

The values are passed in the following format: variableName#TitleName@ValueToInput (aa:#integer ?@10).

So, when you want to fuzz a new file, you can either change the names in the preferences part of the application to the names in the code or change the code to pair the input string with the names in the preferences part of an application.



*How to modify the selenium part:

The web pages of SmartThings change every week. That means the tags in the html codes of the pages are changing. Selenium uses tags to locate the input boxes. Thus, we need to modify the selenium part to follow the new tags of the pages to feed the inputs.

If you want to know which part need to be modified (I don’t know actually), please read the code of the selenium part or contact Fengyao.
