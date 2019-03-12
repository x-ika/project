cd client

jar uf client.jar resources

echo asdqwe13| jarsigner -keystore "C:\Documents and Settings\Workspace\ika_keys" poker-client.jar poker
echo asdqwe13| jarsigner -keystore "C:\Documents and Settings\Workspace\ika_keys" graphictools.jar poker
echo asdqwe13| jarsigner -keystore "C:\Documents and Settings\Workspace\ika_keys" utils.jar poker
echo asdqwe13| jarsigner -keystore "C:\Documents and Settings\Workspace\ika_keys" nettools.jar poker

copy /Y poker-client.jar C:\xampp\htdocs\poker\client\
copy /Y graphictools.jar C:\xampp\htdocs\poker\client\
copy /Y utils.jar        C:\xampp\htdocs\poker\client\
copy /Y nettools.jar     C:\xampp\htdocs\poker\client\
