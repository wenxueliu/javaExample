package org.wenxueliu.serializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JacksonTreeModel {

    String jsonBody =
        new StringBuilder("{")
          .append("\"id\":1,")
          .append("\"name\": {")
          .append(" \"first\" : \"Yong\",")
          .append(" \"last\" : \"Mook Kim\",")
          .append("},")
          .append("\"contact\" : [")
          .append("  { \"type\" : \"phone/home\", \"ref\" : \"111-111-1234\"},")
          .append("  { \"type\" : \"phone/work\", \"ref\" : \"222-222-2222\"}")
          .append("]")
          .append("}").toString();

    String jsonArray =
        new StringBuilder("[{")
          .append("\"id\":1,")
          .append("\"name\": {")
          .append(" \"first\" : \"Yong\",")
          .append(" \"last\" : \"Mook Kim\",")
          .append("},")
          .append("\"contact\" : [")
          .append("  { \"type\" : \"phone/home\", \"ref\" : \"111-111-1234\"},")
          .append("  { \"type\" : \"phone/work\", \"ref\" : \"222-222-2222\"}")
          .append("]")
          .append("},")
          .append("\"id\":2,")
          .append("\"name\": {")
          .append(" \"first\" : \"None\",")
          .append(" \"last\" : \"Kim\",")
          .append("},")
          .append("\"contact\" : [")
          .append("  { \"type\" : \"phone/home\", \"ref\" : \"333-111-1234\"},")
          .append("  { \"type\" : \"phone/work\", \"ref\" : \"444-222-2222\"}")
          .append("]")
          .append("}")
          .append("]")
          .toString();

    public static void parseJsonArray(JsonNode root) {
        if (root.isArray()) {
            for (JsonNode node : root) {
                parseJsonObject(node);
            }
        }
    }

    public static void parseJsonObject(JsonNode root) {
        try {
            long id;
            String firstName = "";
            String middleName = "";
            String lastName = "";
            // Get id
            id = root.path("id").asLong();
            System.out.println("id : " + id);

            // Get Name
            JsonNode nameNode = root.path("name");
            if (nameNode.isMissingNode()) {
            	// if "name" node is missing
            } else {

                firstName = nameNode.path("first").asText();
                // missing node, just return empty string
                middleName = nameNode.path("middle").asText();
                lastName = nameNode.path("last").asText();

                System.out.println("firstName : " + firstName);
                System.out.println("middleName : " + middleName);
                System.out.println("lastName : " + lastName);

                // Get Contact
                JsonNode contactNode = root.path("contact");
                if (contactNode.isArray()) {
                	// If this node an Arrray?
                }

                for (JsonNode node : contactNode) {
                	String type = node.path("type").asText();
                	String ref = node.path("ref").asText();
                	System.out.println("type : " + type);
                	System.out.println("ref : " + ref);
                }
            }
        } catch (JsonGenerationException e) {
        	e.printStackTrace();
        } catch (JsonMappingException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }

    public static void crudJsonObject(JsonNode root) {
		try {

            ((ObjectNode) root).put("id", 1000L);
            JsonNode nameNode = root.path("name");
            if ("".equals(nameNode.path("middle").asText())) {
                ((ObjectNode) nameNode).put("middle", "M");
            }
            ((ObjectNode) nameNode).put("nickname", "mkyong");
            ((ObjectNode) nameNode).remove("last");

            ObjectNode positionNode = mapper.createObjectNode();
            positionNode.put("name", "Developer");
            positionNode.put("years", 10);
            ((ObjectNode) root).set("position", positionNode);

            ArrayNode gamesNode = mapper.createArrayNode();
            ObjectNode game1 = mapper.createObjectNode();
            game1.put("name", "Fall Out 4");
            game1.put("price", 49.9);
            ObjectNode game2 = mapper.createObjectNode();
            game2.put("name", "Dark Soul 3");
            game2.put("price", 59.9);
            gamesNode.add(game1);
            gamesNode.add(game2);
            ((ObjectNode) root).set("games", gamesNode);

            ObjectNode email = mapper.createObjectNode();
            email.put("type", "email");
            email.put("ref", "abc@mkyong.com");

            JsonNode contactNode = root.path("contact");
            ((ArrayNode) contactNode).add(email);

            String resultUpdate = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
            System.out.println("After Update " + resultUpdate);

        } catch (JsonGenerationException e) {
        	e.printStackTrace();
        } catch (JsonMappingException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }

    public static void test() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonBody);
        parseJsonObject(root);

        JsonNode rootArray = mapper.readTree(jsonArray);
        parseJsonArray(rootArray);

        String resultOriginal = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        System.out.println("Before Update " + resultOriginal);
        crudJsonObject(root);
    }

	public static void main(String[] args) {
        test();
	}
}
