package sitemonitor;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
	
	static {
		System.setProperty("spring.mail.host", "localhost");
		System.setProperty("sitemonitor.mail.from", "unittest@unittesting.org");
	}

	@Test
	public void contextLoads() {
	}

}
