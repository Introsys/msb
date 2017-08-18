/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.openmos.model;

// import eu.openmos.agentcloud.data.recipe.SkillRequirement;
import eu.openmos.model.Part;
import eu.openmos.model.SkillRequirement;
import eu.openmos.model.utilities.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import org.bson.Document;

/**
 * Object used to describe a Product. As decided in Masmec meeting in july 2017, the system must have the concept of
 * "product" independently from orders line. With product we mean product model (product A, product B, etc) and not
 * product instance, that is part of the order.
 *
 * The list of product (product models) will come from the MSB. The HMI will have a view for creating orders and sending
 * them down to MSB.
 *
 * Don't know yet if products (product models) need to be stored into the cloud platform database. For now i don't store
 * them. Guess having the class is enough for HMI.
 *
 * @author Valerio Gentile <valerio.gentile@we-plus.eu>
 *
 */
public class Product
{

  /**
   * WP3 semantic model alignment. Id of the product family, of the model of the product (product A, product B, etc)
   */
  private String modelId;
  /**
   * Product model name.
   */
  private String name;
  /**
   * Product model description.
   */
  private String description;
  /**
   * WP3 semantic model alignment. List of parts (components).
   *
   * MSB and WP4 Bari decision: we will not use parts (components) for any demonstrator so far.
   */
  private List<Part> parts;
  /**
   * Skills that need to be executed. List of skill requirements.
   */
  private List<SkillRequirement> skillRequirements;
  /**
   * WP3 semantic model alignment. Timestamp regarding product model.
   */
  private Date registeredTimestamp;

  // private static final int FIELDS_COUNT = 7;
  // private static final int FIELDS_COUNT = 10;
  private static final int FIELDS_COUNT = 6;

  /**
   * Default constructor, for reflection purpose.
   */
  public Product()
  {
  }

  /**
   * Parameterized constructor.
   *
   * @param modelId - product model id
   * @param name - product instance name
   * @param description - product instance description
   * @param parts - list of parts
   * @param skillRequirements - list of skills requirements
   * @param registeredTimestamp - timestamp of object creation
   */
  public Product(String modelId, String name,
          String description, List<Part> parts,
          List<SkillRequirement> skillRequirements, Date registeredTimestamp)
  {
    this.modelId = modelId;
    this.name = name;
    this.description = description;
    this.parts = parts;
    this.skillRequirements = skillRequirements;
    this.registeredTimestamp = registeredTimestamp;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public List<SkillRequirement> getSkillRequirements()
  {
    return skillRequirements;
  }

  public void setSkillRequirements(List<SkillRequirement> skillRequirements)
  {
    this.skillRequirements = skillRequirements;
  }

  public String getModelId()
  {
    return modelId;
  }

  public void setModelId(String modelId)
  {
    this.modelId = modelId;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public List<Part> getParts()
  {
    return parts;
  }

  public void setParts(List<Part> parts)
  {
    this.parts = parts;
  }

  public Date getRegisteredTimestamp()
  {
    return registeredTimestamp;
  }

  public void setRegisteredTimestamp(Date registeredTimestamp)
  {
    this.registeredTimestamp = registeredTimestamp;
  }

  /**
   * /**
   * Method that serializes the object. The returned string has the following format:
   *
   * modelId name, description, list of parts, list of skill requirements, registeredTimestamp ("yyyy-MM-dd
   * HH:mm:ss.SSS")
   *
   * @return Serialized form of the object.
   */
  @Override
  public String toString()
  {
    SimpleDateFormat sdf = new SimpleDateFormat(SerializationConstants.DATE_REPRESENTATION);

    StringBuilder builder = new StringBuilder();
    builder.append(modelId);

    builder.append(SerializationConstants.TOKEN_PRODUCT_DESCRIPTION);
    builder.append(name);

    builder.append(SerializationConstants.TOKEN_PRODUCT_DESCRIPTION);
    builder.append(description);

    builder.append(SerializationConstants.TOKEN_PRODUCT_DESCRIPTION);
    builder.append(ListsToString.writeParts(parts));

    builder.append(SerializationConstants.TOKEN_PRODUCT_DESCRIPTION);
    builder.append(ListsToString.writeSkillRequirements(skillRequirements));

    String stringRegisteredTimestamp = sdf.format(this.registeredTimestamp);
    builder.append(SerializationConstants.TOKEN_PRODUCT_DESCRIPTION);
    builder.append(stringRegisteredTimestamp);

    return builder.toString();
  }

  /**
   * Method that deserializes a String object. The input string needs to have the following format:
   *
   * modelId name, description, list of parts, list of skill requirements, registeredTimestamp ("yyyy-MM-dd
   * HH:mm:ss.SSS")
   *
   * @param object - String to be deserialized.
   * @return Deserialized object.
   * @throws java.text.ParseException
   */
  public static Product fromString(String object) throws ParseException
  {
    StringTokenizer tokenizer = new StringTokenizer(object, SerializationConstants.TOKEN_PRODUCT_DESCRIPTION);

    System.out.println(object);
    if (tokenizer.countTokens() != FIELDS_COUNT)
    {
      throw new ParseException("ProductDescription - " + SerializationConstants.INVALID_FORMAT_FIELD_COUNT_ERROR + FIELDS_COUNT, 0);
    }

    SimpleDateFormat sdf = new SimpleDateFormat(SerializationConstants.DATE_REPRESENTATION);

    String pmi = tokenizer.nextToken();     // model id
    String name = tokenizer.nextToken();    // name
    String description = tokenizer.nextToken();     // description
    List<Part> comps = StringToLists.readParts(tokenizer.nextToken());       // parts
    List<SkillRequirement> skillsReq = StringToLists.readSkillRequirements(tokenizer.nextToken());      // skill requirements
    String registered = tokenizer.nextToken();

    return new Product(
            pmi, // product model id
            name, // name
            description, // description
            comps, // list of parts
            skillsReq, // list of skill requirements
            sdf.parse(registered) // registeredTimestamp
    );

  }

  /**
   * Method that serializes the object into a BSON document. The returned BSON document has the following format:
   *
   * modelId name, description, list of parts, list of skill requirements, registeredTimestamp ("yyyy-MM-dd
   * HH:mm:ss.SSS")
   *
   * @return BSON form of the object.
   */
  public Document toBSON()
  {
    Document doc = new Document();

    List<String> partIds = parts.stream().map(part -> part.getUniqueId()).collect(Collectors.toList());
//        List<String> skillRequirementIds = skillRequirements.stream().map(skillRequirement -> skillRequirement.getUniqueId()).collect(Collectors.toList());        
    List<String> skillRequirementIds = skillRequirements.stream().map(skillRequirement -> skillRequirement.getName()).collect(Collectors.toList());

    doc.append("modelId", modelId);
    doc.append("name", name);
    doc.append("description", description);
    doc.append("parts", partIds);
    doc.append("skillRequirements", skillRequirementIds);
    doc.append("registered", new SimpleDateFormat(SerializationConstants.DATE_REPRESENTATION).format(registeredTimestamp));

    return doc;
  }
}