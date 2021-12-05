This repo was created for the following paper:

"Westworld: Fuzzing-Assisted Remote Dynamic Symbolic Execution of Smart Apps on IoT Cloud Platforms"

Lannan Luo (University of South Carolina), Qiang Zeng (University of South Carolina), Bokai Yang (University of South Carolina), Fei Zuo (University of South Carolina), Junzhe Wang (University of South Carolina)


# Westworld

How to run the remote dynamic symbolic execution tool Westworld:
1. import the project into eclipse or intellij. 
2. import all the jar files in the libary folder (the folder library is under the source folder).
3. download the newest chromedriver and put it in the source folder of Chrome (you need to first install Chrome. There is a chromedriver.exe in the library folder).
4. import com.microsoft.z3.jar under the libary\z3-4.8.4.d6df51951f4c-x64-win\bin
5. Run through the main method in Front.groovy.



How to run the fuzzing tool:

1. Import the whole project (using eclipse or intellij).
2. Import all the jar files in the folder library (the folder library is under the source folder).
3. Download the newest chromedriver.exe and put it in the source folder of Chrome.
4. Modify the inputs part and the selenium part (see below) according to your requirements.
5. Run the main method in Front.groovy.



*How to modify the selenium part:

The web page of SmartThings changes frequently. That means the tags of the html code may be changed. Selenium uses tags to locate the smart app inputs. Thus, we need to modify the selenium part to follow the new tags of the web page to locate the inputs. Please read the code of the selenium part to understand which part needs to be modified.

