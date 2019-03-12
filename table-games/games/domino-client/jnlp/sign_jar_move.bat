cd client

jar uf domino-client.jar resources

FOR %%d IN (*.jar) DO ^
&& jar ufm %%d ../manifest_adder.mf ^
&& echo asdqwe13| jarsigner -keystore "C:\Documents and Settings\%username%\ika_keys.jks" %%d domino ^
&& copy /Y %%d C:\xampp\htdocs\domino\client\
