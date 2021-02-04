package com.atul.gitbook.learn.notes.service;

import com.atul.gitbook.learn.TestBase;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NotesControllerTest extends TestBase {

    @Test
    void testCreateNotesWhenRequesterIsNull() throws Exception {
        fMockMvc.perform(createUserRequest(null, null))
                .andExpect(status().isBadRequest());
    }
}
