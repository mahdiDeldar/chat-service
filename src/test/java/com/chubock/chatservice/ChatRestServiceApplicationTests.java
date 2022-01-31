package com.chubock.chatservice;

import com.chubock.chatservice.conf.TestsContainerApplicationContextInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = {TestsContainerApplicationContextInitializer.class})
class ChatRestServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
