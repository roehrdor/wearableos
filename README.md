## Building GarmentOS 
### From command line
#### 1. Cloning git repository
1. Clone the pearl-elk git repository ( git@gitlab.roehrdor.com:roehrdor/pearl-elk.git )

#### 2. Building Dependencies
##### 1. Building LiveSDK
1. cd .\LiveSDK
2. xcopy /e .\internal .\src
3. android update lib-project -p . -t android-14
4. Open .\LiveSDK\project.properties and make sure there is the "android.library=true" flag set
5. ant clean
6. ant debug
7. cd ..

##### 2. Building Google Play Services
1. cd .\google-play-services_lib
2. android update lib-project -p . -t android-14
3. Open .\google-play-services_lib\project.properties and make sure there is the "android.library=true" flag set
4. ant clean 
5. ant debug
6. cd ..

#### 3. Building GarmentOS App
1. android update project -p . -t android-21 -l google-play-services_lib 
2. android update project -p . -l LiveSDK 
3. ant clean
4. ant debug

#### 4. Reset Git Repository
Since we alter the src folder of the LiveSDK we need to reset it to make further commits or changes to version control    

1. cd .\LiveSDK
2. Windows: rmdir /s /q src     
   Unix: rm -rf ./src
3. cd ..
4. git reset --hard HEAD