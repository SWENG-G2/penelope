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
public class CampusEndpointsTests {
    private static final String ADMIN_KEY = "1337";
    private static final String ADMIN_OWNER = "Odysseus";
    private static final String KEY = "1234";
    private static final String OWNER = "Telemachus";
    private static final String TEST_CAMPUS_NAME = "New University";
    private static final String BAD_KEY = "5678";
    private ApiKey testAdminApiKey, testApiKey;

    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() {
        // Inject admin test key
        testAdminApiKey = new ApiKey();
        testAdminApiKey.setIdentity(ADMIN_KEY);
        testAdminApiKey.setOwnerName(ADMIN_OWNER);
        testAdminApiKey.setAdmin(true);

        apiKeyRepository.save(testAdminApiKey);

        // Inject non admin key
        testApiKey = new ApiKey();
        testApiKey.setIdentity(KEY);
        testApiKey.setOwnerName(OWNER);

        apiKeyRepository.save(testApiKey);
    }

    @AfterAll
    public void cleanUp() {
        // Remove admin test key
        apiKeyRepository.delete(testAdminApiKey);
        // Remove non admin test key
        apiKeyRepository.delete(testApiKey);
    }

    @Test
    public void sysadminCanCreateAndDelete() throws Exception {
        MockHttpServletResponse writeResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/campus/new").param("name", TEST_CAMPUS_NAME).param("apiKey", ADMIN_KEY))
                .andReturn().getResponse();

        // Get id of campus. Response contains ...(id: %id)
        String testCampusID = writeResponse.getContentAsString().split(":")[1].split("\\)")[0];

        // 200 is OK
        assertEquals(200, writeResponse.getStatus());

        MockHttpServletResponse deleteResponse = mockMvc.perform(
                MockMvcRequestBuilders.delete("/campus/remove").param("apiKey", ADMIN_KEY).param("id", testCampusID))
                .andReturn().getResponse();

        // 200 is OK
        assertEquals(200, deleteResponse.getStatus());
    }

    @Test
    public void anyoneCannotCreate() throws Exception {
        MockHttpServletResponse writeResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/campus/new").param("apiKey", KEY).param("name", TEST_CAMPUS_NAME))
                .andReturn().getResponse();

        // 401 is UNAUTHORISED
        assertEquals(401, writeResponse.getStatus());

        writeResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/campus/new").param("apiKey", BAD_KEY).param("name", TEST_CAMPUS_NAME))
                .andReturn().getResponse();

        // 401 is UNAUTHORISED
        assertEquals(401, writeResponse.getStatus());
    }

    @Test
    public void anyoneCannotDelete() throws Exception {
        MockHttpServletResponse deleteResponse = mockMvc.perform(
                MockMvcRequestBuilders.delete("/campus/remove").param("apiKey", KEY).param("id", "1")) // random id
                .andReturn().getResponse();

        // 401 is UNAUTHORISED
        assertEquals(401, deleteResponse.getStatus());

        deleteResponse = mockMvc.perform(
                MockMvcRequestBuilders.delete("/campus/remove").param("apiKey", BAD_KEY).param("id", "1"))
                .andReturn().getResponse();

        // 401 is UNAUTHORISED
        assertEquals(401, deleteResponse.getStatus());
    }

    @Test
    public void anyoneCanRead() throws Exception {
        MockHttpServletResponse getResponse = mockMvc.perform(MockMvcRequestBuilders.get("/campus/all")).andReturn()
                .getResponse();

        // 200 is OK
        assertEquals(200, getResponse.getStatus());
    }

    @Test
    public void sysadminCannotDeleteUnexistingCampus() throws Exception {
        MockHttpServletResponse deleteResponse = mockMvc.perform(
                MockMvcRequestBuilders.delete("/campus/remove").param("apiKey", ADMIN_KEY).param("id", "99")) // random id
                .andReturn().getResponse();

        // 404 is Not Found
        assertEquals(404, deleteResponse.getStatus());
    }
}
