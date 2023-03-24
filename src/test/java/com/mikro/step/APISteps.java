package com.mikro.step;


import com.mikro.base.BaseTest;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class APISteps extends BaseTest {


    Map<String, String> headers = new HashMap<>();
    HashMap<String,Object> hashMap=new HashMap<String,Object>();

    JSONObject jObject = null;
    Response response = null;
    public static final String BASE_URL="https://petstore.swagger.io/v2/";


    @BeforeScenario
    public void before() {
        RestAssured.baseURI = BASE_URL;
    }


    @Step("Jobject Oluştur")
    public void createJObject()
    {
        jObject = new JSONObject();
        logger.info("Yeni bir JObject Olusturuldu");
    }

    @Step("<key> key ve <value> value degerini JObjecte ekle")
    public void addToRequestBody(String key, String value)
    {
        jObject.put(key, value);
        logger.info("JObject'e " + key + ":" + value + " degeri eklendi");
    }

    @Step("<api> apiye <type> methoduyla istek at")
    public void setApi(String api,String type)
    {
        logger.info(RestAssured.baseURI + RestAssured.basePath + " servisine " + type + " istegi atildi");
        if(type.equals("post"))
        {
            response= RestAssured.given().headers(headers)
                    .contentType(ContentType.JSON)
                    .body(jObject.toString())
                    .post(api);
        }
        else if(type.equals("put"))
        {
            response= RestAssured.given().headers(headers)
                    .contentType(ContentType.JSON)
                    .body(jObject.toString())
                    .put(api);
        }
        else if(type.equals("get"))
        {
            response= RestAssured.given().headers(headers)
                    .contentType(ContentType.JSON)
                    .queryParam(jObject.toString())
                    .get(api);
        }
        else if(type.equals("delete"))
        {
            response= RestAssured.given().headers(headers)
                    .contentType(ContentType.JSON)
                    .body(jObject.toString())
                    .delete(api);
        }
        else {
            logger.info("Lütfen geçerli bir deger giriniz");
        }
        logger.info("Request : " + jObject.toString());
        logger.info("Response : " + response.getBody().asString());
    }

    @Step("status kod <statusCode> ile ayni mi kontrol et")
    public void checkStatusCode2(int statusCode)
    {
        Assertions.assertEquals(statusCode, response.getStatusCode(), "Status code eslesmiyor...");
        logger.info("status kod " + response.getStatusCode() + " ile ayni mi kontrol edildi");
    }

    @Step("response <key> alanı <value> iceriyor mu kontrol et")
    public void checkResponseMethod(String key, String value) {
        if (value.contains("null")) {
            Assertions.assertNull(response.jsonPath().get(key), key + " değeri null değil");
        } else {
            Assertions.assertTrue(value.equals(response.jsonPath().get(key).toString()), value + " degeri ile " + response.jsonPath().getString(key) + " degeri uyusmuyor");
        }
        logger.info("response " + key + " alani  " + value + " iceriyor mu kontrol edildi");
    }

    @Step("response <key> keyinin degerini <hashmapKey> olarak kaydet")
    public void responseHashmap(String key, String hashmapKey) {
        hashMap.put(hashmapKey, response.jsonPath().get(key).toString());
        logger.info("response " + key + " keyinin degerini " + hashmapKey + " olarak kaydedildi");
    }

    @Step("<key> keyine hashmapdeki <value> keyli değeri JObjecte ekle")
    public void AddElementHashmapToRest(String key,String value){
        jObject.put(key, hashMap.get(value).toString());
        logger.info("JObject'e " + key + ":" + hashMap.get(value).toString() + " degeri eklendi");
    }

    @Step("<key> key <value> value degerini headera ekle")
    public void addHeader(String key, String value)
    {
        headers.put(key,value);
        logger.info("Header'a " +key+ "," +value+ " degeri eklendi");
    }

    @Step("<key> keyli <value> degeri hashmap'e ekle")
    public void addHashmapManuel(String key, String value)
    {
        hashMap.put(key, value);
        logger.info(key + " keyli " + value + " degeri manuel olarak hashmap'e eklendi");
    }

    @Step("Hashmapin icindeki <hashmapKey> keyinin degeri <hashmapKey2> keyinin degeri ile <type> mı kontrol et")
    public void checkDifferenceHashmap(String hashmapKey, String hashmapKey1, String type)
    {
        if ("aynı".equals(type))
        {
            Assertions.assertEquals(hashMap.get(hashmapKey).toString(), hashMap.get(hashmapKey1).toString(), "hashmapteki degerler eslesiyor...");
            logger.info(hashMap.get(hashmapKey).toString() + ", " + hashMap.get(hashmapKey1).toString() + " ile " + type + " mi kontrol edildi.");
        }
        else if ("farklı".equals(type))
        {
            Assertions.assertNotEquals(hashMap.get(hashmapKey).toString(), hashMap.get(hashmapKey1).toString(), "hashmapteki degerler eslesmiyor...");
            logger.info(hashMap.get(hashmapKey).toString()+" , "+ hashMap.get(hashmapKey1).toString() + " ile " + type + " mi kontrol edildi");
        }
        else
        {
            Assertions.fail("Lütfen Gecerli bir tip giriniz");
        }
    }

}