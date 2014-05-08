package com.amazon.aws.support;

import com.amazonaws.internal.EC2MetadataClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfig {

  private final String dynamoDbTable;
  private final AmazonDynamoDBClient dynamoDbClient;
  private final EC2MetadataClient ec2MetadataClient;
  
  private final String instanceId;
  private final String availabilityZone;

  private static ApplicationConfig instance;

  private ApplicationConfig() throws IOException {

    Properties prop = new Properties();
    File propFile = new File("/etc/application.properties");
    InputStream in = new FileInputStream(propFile);
    prop.load(in);
    
    ec2MetadataClient = new EC2MetadataClient();
    instanceId = ec2MetadataClient.readResource("/latest/meta-data/instance-id");
    availabilityZone = ec2MetadataClient.readResource("/latest/meta-data/placement/availability-zone");

    dynamoDbTable = prop.getProperty("dynamodb.table");
    dynamoDbClient = new AmazonDynamoDBClient();
  }

  public static ApplicationConfig getInstance() throws IOException {

    if (instance == null) {
      instance = new ApplicationConfig();
    }

    return instance;
  }

  public String getDynamoDbTable() {
    return dynamoDbTable;
  }

  public AmazonDynamoDBClient getDynamoDbClient() {
    return dynamoDbClient;
  }

  public EC2MetadataClient getEc2MetadataClient() {
    return ec2MetadataClient;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public String getAvailabilityZone() {
    return availabilityZone;
  }
  
}
