import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SpotifyAPITesting {
    String token="Bearer BQA5cf2HCuEIGDDGW0Oe_GECgUxa2Ze9r3dXQdV16xH8_tsmay8dGdBZVoHost1EcGJ5oSbvQzvrQ0uUeRehiHYabUqfEZW6R_BrTikWxozq7k1QdSOT9Wfhsf9KaNZRoDnQiUIoPac0TEx6hXKtsRS9sScXJkiaPHUoK53h8RTyDj4kTrXSZist18KcE3BDM4n-lIsY1dAa7qnvbEFsKUPk76QuwYxQ1FaeCjjP7roh_SLLHCn4BG67jGXwJ_eu69RbhPdAv5NJsQuprKGzXqz-Q83Udw";
    String userId="";
  @Before
  public void setUp(){
      RestAssured.baseURI = "https://api.spotify.com";
  }

  @Test
    public void givenMethod_WhenCountPlaylistBeforeAdding_ThenShouldReturnTotalPlaylist() {
        Response response = RestAssured.given()
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .when()
                .get("/v1/me/playlists");
        ResponseBody body = response.getBody();
        Object object = new JsonParser().parse(body.prettyPrint());
        int playlistCount = response.path("total");
        Assert.assertEquals(95,playlistCount);
    }

    @Test
    public void givenMethod_WhenGetUserProfile_ThenShouldReturnUserIdandUserProfile() {
        // To get the user profile
        Response responseOfUserProfile = RestAssured.given()
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .when()
                .get("/v1/me");
        ResponseBody userProfileBody = responseOfUserProfile.getBody();
        JsonObject object1 = (JsonObject) new JsonParser().parse(userProfileBody.prettyPrint());
        userId = responseOfUserProfile.path("id");
        System.out.println("User id is "+userId);
        responseOfUserProfile.then().assertThat().statusCode(200);

        //To get user profile by using id
        Response responseOfUserProfileById = RestAssured.given()
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .when()
                .get("/v1/users/"+userId);
        ResponseBody userIdBody = responseOfUserProfileById.getBody();
        Object object2 = new JsonParser().parse(userIdBody.prettyPrint());
        responseOfUserProfile.then().assertThat().statusCode(200);


        //To add player List
        Response responseForAddList = RestAssured.given()
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .body("{\"name\": \"Heart touch Songs\",\"description\": \"Latest song\",\"public\": \"false\"}")
                .when()
                .post("/v1/users/"+userId+"/playlists");
        ResponseBody responseBody = responseForAddList.getBody();
        Object jsonObject = new JsonParser().parse(responseBody.prettyPrint());
        responseForAddList.then().assertThat().statusCode(201);
    }

    @Test
    public void givenMethod_WhenGetCountPlaylistAfterAdding_ThenShouldReturnTotalPlaylist() {
        Response response = RestAssured.given()
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .when()
                .get("/v1/me/playlists");
        ResponseBody body = response.getBody();
        Object object = new JsonParser().parse(body.prettyPrint());
        int playListCount = response.path("total");
        System.out.println("Total count is "+playListCount);
        Assert.assertEquals(96,playListCount);
    }

    //To get playlist Id And Update Playlist
    @Test
    public void givenMethod_WhenGetPlaylistIdAndUpdatePlaylist_ThenShouldReturnUpdateList() {
        Response response = RestAssured.given()
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .when()
                .get("/v1/me/playlists");
        ResponseBody body = response.getBody();
        Object object = new JsonParser().parse(body.prettyPrint());
        String resAs = response.asString();
        JsonPath json = new JsonPath(resAs);
        String playlistId = json.getString("items[1].id");
        System.out.println("Playlist id is "+playlistId);

        //To updating playlist
        Response responseOfPlaylist = RestAssured.given()
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .body("{\"name\": \"Neha Kakkar Songs\",\"description\": \"Latest song\",\"public\": false}")
                .when()
                .put("/v1/playlists/"+playlistId);
        ResponseBody responseBody = responseOfPlaylist.getBody();
        Object jsonElement = new JsonParser().parse(responseBody.prettyPrint());
        responseOfPlaylist.then().assertThat().statusCode(200);

        //To get updated playlist
        Response res = RestAssured.given()
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .when()
                .get("/v1/me/playlists");
        ResponseBody bodyToGetPlaylist = res.getBody();
        Object object1 = new JsonParser().parse(bodyToGetPlaylist.prettyPrint());
    }
}
