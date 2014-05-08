package com.amazon.aws.support;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageCountServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws IOException {

    response.setContentType("text/html");

    PrintWriter out = response.getWriter();
    out.println("<html>");
    out.println("<body>");
    out.println("<h1>Hello world!</h1>");
    out.println("<ul>");
    out.println("  <li>Instance ID: " + ApplicationConfig.getInstance().getInstanceId() + "</li>");
    out.println("  <li>Availability Zone: " + ApplicationConfig.getInstance().getAvailabilityZone() + "</li>");
    out.println("</ul>");
    out.println("This page has been accessed " + getAndIncrementPageCount() + " times.");
    out.println("</body>");
    out.println("</html>");
  }

  public int getAndIncrementPageCount() throws IOException {

    String tableName = ApplicationConfig.getInstance().getDynamoDbTable();
    AmazonDynamoDBClient dynamoDbClient = ApplicationConfig.getInstance().getDynamoDbClient();

    int currentCount = 0;
    
    // Get the current count
    Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
    key.put("page_name", new AttributeValue().withS("default"));

    GetItemRequest getItemRequest = new GetItemRequest()
            .withTableName(tableName)
            .withKey(key)
            .withAttributesToGet(Arrays.asList("count"));

    GetItemResult result = dynamoDbClient.getItem(getItemRequest);
    Map<String, AttributeValue> value = result.getItem();
    
    // Get the returned count
    AttributeValue counterValue = value.get("count");
    if (counterValue != null) {
      currentCount = Integer.parseInt(counterValue.getN());
    }

    // Increment the counter
    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
    updateItems.put("count",
            new AttributeValueUpdate()
            .withAction(AttributeAction.ADD)
            .withValue(new AttributeValue().withN("1")));

    ReturnValue returnValues = ReturnValue.ALL_NEW;
    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
            .withTableName(tableName)
            .withKey(key)
            .withAttributeUpdates(updateItems)
            .withReturnValues(returnValues);
    dynamoDbClient.updateItem(updateItemRequest);
    
    return currentCount;
  }

}
