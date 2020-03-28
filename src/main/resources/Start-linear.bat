@echo on
echo Starting RMIRegistry...
START /MAX RMIRegistry.bat
echo RMIRegistry started...
timeout /t 5


echo Starting Clients...

echo Starting Client 1...
cd Clients\Client1
START /MAX Client1-linear.bat
echo Client1 started...
timeout /t 2

echo Starting Client 4...
cd ..\Client4
START /MAX Client4-linear.bat
echo Client4 started...
timeout /t 2

echo Starting Client 7...
cd ..\Client7
START /MAX Client7-linear.bat
echo Client7 started...
timeout /t 60

echo Starting Client 2...
cd ..\Client2
START /MAX Client2-linear.bat
echo Client2 started...
timeout /t 2

echo Starting Client 3...
cd ..\Client3
START /MAX Client3-linear.bat
echo Client3 started...
timeout /t 2

echo Starting Client 5...
cd ..\Client5
START /MAX Client5-linear.bat
echo Client5 started...
timeout /t 2

echo Starting Client 6...
cd ..\Client6
START /MAX Client6-linear.bat
echo Client6 started...
timeout /t 2

echo Starting Client 8...
cd ..\Client8
START /MAX Client8-linear.bat
echo Client8 started...
timeout /t 2

echo Starting Client 9...
cd ..\Client9
START /MAX Client9-linear.bat
echo Client9 started...
timeout /t 2

echo Exiting...
@echo off