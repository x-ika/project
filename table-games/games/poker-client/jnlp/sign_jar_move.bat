cd client

jar uf client.jar resources

echo asdqwe13| jarsigner -keystore "C:\Documents and Settings\Workspace\ika_keys" client.jar poker
echo asdqwe13| jarsigner -keystore "C:\Documents and Settings\Workspace\ika_keys" graphictools.jar poker
echo asdqwe13| jarsigner -keystore "C:\Documents and Settings\Workspace\ika_keys" sysutils.jar poker
echo asdqwe13| jarsigner -keystore "C:\Documents and Settings\Workspace\ika_keys" netutils.jar poker

copy /Y client.jar       C:\xampp\htdocs\poker\client\
copy /Y graphictools.jar C:\xampp\htdocs\poker\client\
copy /Y sysutils.jar     C:\xampp\htdocs\poker\client\
copy /Y netutils.jar     C:\xampp\htdocs\poker\client\
