package sweng.penelope;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import sweng.penelope.entities.ApiKey;
import sweng.penelope.repositories.ApiKeyRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class DuckEndpointsTests {
    private static final String KEY = "1337";
    private static final String BAD_KEY = "1234";
    private static final String OWNER = "Odysseus";
    private static final String TEST_DUCK_NAME = "The testing duck";
    private static final String TEST_DUCK_DESCRIPTION = "\"Testing is great fun\", said no full-stack developer. Ever.";
    private ApiKey testApiKey;

    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() {
        // Inject test key
        testApiKey = new ApiKey();
        testApiKey.setKey(KEY);
        testApiKey.setOwnerName(OWNER);

        apiKeyRepository.save(testApiKey);
    }

    @AfterAll
    public void cleanUp() {
        // Remove test key
        apiKeyRepository.delete(testApiKey);
    }

    @Test
    public void authUserCanCreateAndDelete() throws Exception {
        MockHttpServletResponse writResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/ducks/new").param("apiKey", KEY).param("name", TEST_DUCK_NAME).param(
                        "description", TEST_DUCK_DESCRIPTION))
                .andReturn().getResponse();

        // Get id of duck. Response contains ...(id: %id)
        String testDuckID = writResponse.getContentAsString().split(":")[1].split("\\)")[0];
        // 200 is OK
        assertEquals(200, writResponse.getStatus());

        MockHttpServletResponse deleteResponse = mockMvc.perform(
                MockMvcRequestBuilders.delete("/ducks/remove").param("apiKey", KEY).param("id", testDuckID))
                .andReturn().getResponse();

        // 200 is OK
        assertEquals(200, deleteResponse.getStatus());
    }

    @Test
    public void anyoneCannotCreate() throws Exception {
        MockHttpServletResponse writResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/ducks/new").param("apiKey", BAD_KEY).param("name", TEST_DUCK_NAME).param(
                        "description", TEST_DUCK_DESCRIPTION))
                .andReturn().getResponse();

        // 401 is UNAUTHORISED
        assertEquals(401, writResponse.getStatus());
    }

    @Test
    public void anyoneCannotDelete() throws Exception {
        MockHttpServletResponse deleteResponse = mockMvc.perform(
                MockMvcRequestBuilders.delete("/ducks/remove").param("apiKey", BAD_KEY).param("id", "10")) // random id
                .andReturn().getResponse();

        // 401 is UNAUTHORISED
        assertEquals(401, deleteResponse.getStatus());
    }

    @Test
    public void anyoneCanGetListOfDucks() throws Exception {
        MockHttpServletResponse getResponse = mockMvc.perform(
                MockMvcRequestBuilders.get("/ducks/all"))
                .andReturn().getResponse();

        // 200 is OK
        assertEquals(200, getResponse.getStatus());
    }
}
