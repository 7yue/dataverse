package edu.harvard.iq.dataverse.api;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

public class Util {
    
    static final Set<String> booleanValues;
    static final Set<String> booleanTrueValues;
    
    static {
        booleanTrueValues = new TreeSet<>();
        booleanTrueValues.add("true");
        booleanTrueValues.add("yes");
        booleanTrueValues.add("1");
        
        booleanValues = new TreeSet<>();
        booleanValues.addAll( booleanTrueValues );
        booleanValues.add("no");
        booleanValues.add("false");
        booleanValues.add("0");
    }

    static String jsonObject2prettyString(JsonObject jsonObject) {
        Map<String, Boolean> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = jsonWriterFactory.createWriter(stringWriter)) {
            jsonWriter.writeObject(jsonObject);
        }
        return stringWriter.toString();
    }

    static String jsonArray2prettyString(JsonArray jsonArray) {
        Map<String, Boolean> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = jsonWriterFactory.createWriter(stringWriter)) {
            jsonWriter.writeArray(jsonArray);
        }
        return stringWriter.toString();
    }

    static String message2ApiError(String message) {
        JsonObject error = Json.createObjectBuilder()
                .add("message", message)
                .add("documentation_url", "http://thedata.org")
                .build();
        return jsonObject2prettyString(error);

    }
	
    static JsonArray asJsonArray( String str ) {
        try ( JsonReader rdr = Json.createReader(new StringReader(str)) ) {
            return rdr.readArray();
        }
    }
    
	static String ok( JsonObject obj ) {
		JsonObjectBuilder response = Json.createObjectBuilder();
		response.add("status", "OK");
		response.add("data", obj);
		
		return jsonObject2prettyString(response.build());
	}
	
	static String ok( String msg ) {
		JsonObjectBuilder response = Json.createObjectBuilder();
		response.add("status", "OK");
		response.add("data", Json.createObjectBuilder().add("message", msg));
		
		return jsonObject2prettyString(response.build());
	}
	
	static String ok( JsonArray arr ) {
		JsonObjectBuilder response = Json.createObjectBuilder();
		response.add("status", "OK");
		response.add("data", arr);
		
		return jsonObject2prettyString(response.build());
	}
	
	static String error( String message ) {
		JsonObjectBuilder response = Json.createObjectBuilder();
		response.add("status", "ERROR");
		response.add("message", message);
		
		return jsonObject2prettyString(response.build());
	}
	
    static boolean isBoolean( String s ) {
        return booleanValues.contains(s.toLowerCase());
    }
    
    static boolean isTrue( String s ) {
        return booleanTrueValues.contains(s.toLowerCase());
    }
    
	static boolean isNumeric( String s ) {
		for ( char c : s.toCharArray() ) {
			if ( ! Character.isDigit(c) ) return false;
		}
		return true;
	}
}
