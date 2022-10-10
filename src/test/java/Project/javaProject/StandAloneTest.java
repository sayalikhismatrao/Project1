package Project.javaProject;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import io.github.bonigarcia.wdm.WebDriverManager;

public class StandAloneTest {

public static void main(String[] args) throws InterruptedException {
		
		// using webdriver manager to invoke the chrome driver. it doesn't need chrome driver in local
		//1. add maven webdrivermanager dependencies in POm.xlm file
		//2. invoke browser in java program
		
		WebDriverManager.chromedriver().setup(); // replaces system.setProperty();
		WebDriver driver=new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get("https://rahulshettyacademy.com/client");
		//Login with credentials
		//Landingpage loginPage=new Landingpage(driver);
		driver.findElement(By.id("userEmail")).sendKeys("sayalik1992@gmail.com");
		driver.findElement(By.id("userPassword")).sendKeys("P@ssw0rd");
		driver.findElement(By.name("login")).click();
		
		// add item to cart
		//1. get all the products in list
		WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(5));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mb-3")));
		List<WebElement> products=driver.findElements(By.cssSelector(".mb-3")); 
		//2. retrieve item from that list using Stream()
		WebElement item=products.stream().filter(product->product.findElement(By.cssSelector("b")).getText().equals("ZARA COAT 3")).findFirst().orElse(null);
		//3. click on add to cart
		item.findElement(By.cssSelector(".card-body button:last-of-type")).click();
		
		//verify added to cart message and for that we need to add explicit wait	
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#toast-container")));
		//wait until that process bar is disappearing
		wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.cssSelector(".ng-animating"))));
		driver.findElement(By.cssSelector("[routerlink*='cart']")).click();
		
		//to verify product added in cart using stream
		List<WebElement> cartItem=driver.findElements(By.cssSelector(".cartSection h3"));
		Boolean match= cartItem.stream().anyMatch(cartlist->cartlist.getText().equalsIgnoreCase("ZARA COAT 3"));
		//Assert.assertEquals(match, true);
		Assert.assertTrue(match);
		
		//click on checkout button
		JavascriptExecutor js=(JavascriptExecutor)driver;
		js.executeScript("window.scrollBy(0,500)");
		driver.findElement(By.cssSelector(".totalRow button")).click();
		
		//payment process
		Actions a =new Actions(driver);
		a.sendKeys(driver.findElement(By.xpath("//input[@placeholder='Select Country']")), "India").build().perform();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ta-results")));
		driver.findElement(By.xpath("//button[contains(@class,'ta-item')][2]")).click();
		js.executeScript("window.scrollBy(0,1000)");
		Thread.sleep(2000);
		driver.findElement(By.cssSelector(".actions a")).click();
		
		//order confirmation
		String message=driver.findElement(By.cssSelector(".hero-primary")).getText();
		Assert.assertTrue(message.equalsIgnoreCase("THANKYOU FOR THE ORDER."));
	}
}
