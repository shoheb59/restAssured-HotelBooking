import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class RestBookingTest {

    private static String token;
    private static final String BASE_URL = "https://restful-booker.herokuapp.com";

    @BeforeEach
    public void setup()
    {
        RestAssured.baseURI = BASE_URL;
        if(token == null)
        {
            createToken();
        }
    }

    //Test Case to Create Auth Token

    @Test
    public void createToken()
    {
        Response response =
                given()
                        .header("Content-Type","application/json")
                        .body("{\n" +
                                "    \"username\" : \"admin\",\n" +
                                "    \"password\" : \"password123\"\n" +
                                "}")
                        .when()
                        .post("/auth")
                        .then()
                        .statusCode(200)
                        .extract().response();

        token = response.jsonPath().getString("token");
        System.out.println("token is: " +token);
    }

    @Test
    public void createBooking()
    {
        String bookingJsonBody = "{\n" +
                "    \"firstname\": \"Hasnat\",\n" +
                "    \"lastname\": \"Shoheb\",\n" +
                "    \"totalprice\" : 500,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2024-08-01\",\n" +
                "        \"checkout\" : \"2034-08-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Lunch\"\n" +
                "\n" +
                "\n" +
                "}";
        Response response =
                given()
                        .header("Content-Type","application/json")
                        .body(bookingJsonBody)
                        .when()
                        .post("/booking")
                        .then()
                        .statusCode(200)
                        .extract().response();
        int bookingId = response.jsonPath().getInt("bookingid");
        System.out.println("Created booking id: " + bookingId +"\n");

        String resBody =  response.getBody().asString();
        System.out.println("Response Body: " + resBody);
    }

    @Test
    public void getSingleBooking()
    {
        int boookingID =  2;
        Response response =  given()
                .header("Accept", "application/json")
                .when()
                .get("/booking/" + boookingID)
                .then()
                .statusCode(200)
                .extract().response();

        //Validation
        String firstName =  response.jsonPath().getString("firstname");
        String lastName =  response.jsonPath().getString("lastname");
        int totalPrice =  response.jsonPath().getInt("totalprice");
        boolean deposit = response.jsonPath().getBoolean("depositpaid");


        String resBody =  response.getBody().asString();
        System.out.println("Response Body: " + resBody);

        System.out.println("Booking id: " +boookingID);
        System.out.println("Firstname: " + firstName);
        System.out.println("Lastname: "+lastName);
        System.out.println("Total Price: " +totalPrice);
        System.out.println("Deposit Paid: " + deposit);





    }

    @Test
    public void updateBooking()
    {
        int bookingId =2;
        String updatedBookingbody = "{\n" +
                "    \"firstname\": \"Aryan\",\n" +
                "    \"lastname\": \"Shoheb\",\n" +
                "    \"totalprice\" : 50,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2024-08-01\",\n" +
                "        \"checkout\" : \"2024-08-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Lunch\"\n" +
                "\n" +
                "\n" +
                "}";

        Response response = given()
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .header("Cookie","token="+token)
                .body(updatedBookingbody)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .extract().response();


        String resBody = response.getBody().asString();
        System.out.println("Updated Boking Response: " +resBody);

        //Validate the response
        String updatedFirstName = response.jsonPath().getString("firstname");
        System.out.println("updated First Name: " +updatedFirstName);

        assertNotNull(updatedFirstName, "Updated firstname should not be null");
    }

    @Test
    public void partialUpdateBooking()
    {
        int bookingID = 5;
        String partialUpdateBody = "{\n" +
                "    \"firstname\" : \"Hasnat\",\n" +
                "    \"lastname\" : \"Brown\"\n" +
                "}";
        Response response = given()
                .header("Content-Type", "application/json", "Accept","application/json")
                .header("Cookie","token="+token)
                .body(partialUpdateBody)
                .when()
                .patch("/booking/" +bookingID)
                .then()
                .statusCode(200)
                .extract().response();

        String reponseBody = response.getBody().asString();
        System.out.println("Partial updated  first and last name: " +reponseBody);

        String updatedFristName =  response.jsonPath().getString("firstname");
        String updatedLastName = response.jsonPath().getString("lastname");

        assertEquals(updatedFristName,"Hasnat", "First name should be updated to Hasnat");
        assertEquals(updatedLastName, "Brown", "Last name should be updated to Brown");
        System.out.println("Passed");

        }

        @Test
    public void deleteBooking()
        {
            int deleteId = 3;
            Response response = given()
                    .header("Content-Type", "application/json")
                    .header("Cookie", "token="+token)
                    .when()
                    .delete("/booking/"+deleteId)
                    .then()
                    .statusCode(201)
                    .extract().response();

            System.out.println("Delete response status code:" +response.getStatusCode());
            System.out.println("Delete response body: " +response.getBody().asString());
        }

}
