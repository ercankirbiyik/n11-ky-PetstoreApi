Petstore API
============
Created by Ercan Kirbiyik

Petstore HashMap Post
---------------------
* "ad" keyli "ercan" degeri hashmap'e ekle
* "soyad" keyli "kirbiyik" degeri hashmap'e ekle
* Jobject Oluştur
* "id" key ve "1" value degerini JObjecte ekle
* "username" keyine hashmapdeki "ad" keyli değeri JObjecte ekle
* "firstName" keyine hashmapdeki "soyad" keyli değeri JObjecte ekle
* "user" apiye "post" methoduyla istek at
* status kod "200" ile ayni mi kontrol et
* response "code" keyinin degerini "IDcodee" olarak kaydet
* Jobject Oluştur
* "id" keyine hashmapdeki "IDcodee" keyli değeri JObjecte ekle
* "username" keyine hashmapdeki "ad" keyli değeri JObjecte ekle
* "firstName" keyine hashmapdeki "soyad" keyli değeri JObjecte ekle
* "user" apiye "post" methoduyla istek at
* status kod "200" ile ayni mi kontrol et

Petstore Get
------------
* Jobject Oluştur
* "pet/12" apiye "get" methoduyla istek at
* status kod "200" ile ayni mi kontrol et

Petstore Delete
---------------
* Jobject Oluştur
* "pet/12" apiye "delete" methoduyla istek at
* status kod "200" ile ayni mi kontrol et

Petstore varolmayan bir id için get isteği atma
-----------------------------------------------
* Jobject Oluştur
* "pet/0" apiye "get" methoduyla istek at
* status kod "404" ile ayni mi kontrol et

Petstore yanlış urle get isteği atma
------------------------------------
* "peut/1" apiye "get" methoduyla istek at
* status kod "404" ile ayni mi kontrol et


