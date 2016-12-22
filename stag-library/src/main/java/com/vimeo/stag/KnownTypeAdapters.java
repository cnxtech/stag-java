package com.vimeo.stag;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.gson.stream.JsonToken.BEGIN_OBJECT;

public class KnownTypeAdapters {

    public static final TypeAdapter<Byte> BYTE = new TypeAdapter<Byte>() {
        @Override
        public Byte read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                int intValue = in.nextInt();
                return (byte) intValue;
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public void write(JsonWriter out, Byte value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapter<Short> SHORT = new TypeAdapter<Short>() {
        @Override
        public Short read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return (short) in.nextInt();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public void write(JsonWriter out, Short value) throws IOException {
            out.value(value);
        }
    };

    public static final TypeAdapter<Integer> INTEGER = new TypeAdapter<Integer>() {
        @Override
        public Integer read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return in.nextInt();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public void write(JsonWriter out, Integer value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapter<Long> LONG = new TypeAdapter<Long>() {
        @Override
        public Long read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return in.nextLong();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public void write(JsonWriter out, Long value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapter<Float> FLOAT = new TypeAdapter<Float>() {
        @Override
        public Float read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return (float) in.nextDouble();
        }

        @Override
        public void write(JsonWriter out, Float value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapter<Double> DOUBLE = new TypeAdapter<Double>() {
        @Override
        public Double read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return in.nextDouble();
        }

        @Override
        public void write(JsonWriter out, Double value) throws IOException {
            out.value(value);
        }
    };

    public static final TypeAdapter<ArrayList<Integer>> INTEGER_ARRAY_LIST_ADAPTER = new ListTypeAdapter<>(INTEGER, new ArrayListInstantiater<Integer>());
    public static final TypeAdapter<ArrayList<Long>> LONG_ARRAY_LIST_ADAPTER = new ListTypeAdapter<>(LONG, new ArrayListInstantiater<Long>());
    public static final TypeAdapter<ArrayList<Double>> DOUBLE_ARRAY_LIST_ADAPTER = new ListTypeAdapter<>(DOUBLE, new ArrayListInstantiater<Double>());
    public static final TypeAdapter<ArrayList<Short>> SHORT_ARRAY_LIST_ADAPTER = new ListTypeAdapter<>(SHORT, new ArrayListInstantiater<Short>());

    public interface PrimitiveArrayConstructor<T> {
        T[] construct(int size);
    }


    public static final class ArrayTypeAdapter<T extends Object> extends TypeAdapter<T[]> {
        TypeAdapter<T> mValueTypeAdapter;
        PrimitiveArrayConstructor<T> mObjectCreator;

        public ArrayTypeAdapter(TypeAdapter<T> valueTypeAdapter, PrimitiveArrayConstructor<T> instanceCreator) {
            this.mValueTypeAdapter = valueTypeAdapter;
            this.mObjectCreator = instanceCreator;
        }

        @Override
        public void write(JsonWriter writer, T[] value) throws IOException {
            writer.beginArray();
            for (T item : value) {
                mValueTypeAdapter.write(writer, item);
            }
            writer.endArray();
        }

        @Override
        public T[] read(JsonReader reader) throws IOException {
            if (reader.peek() == com.google.gson.stream.JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            if (reader.peek() != BEGIN_OBJECT) {
                reader.skipValue();
                return null;
            }
            reader.beginObject();

            ArrayList<T> object = new ArrayList<>();

            while (reader.hasNext()) {
                com.google.gson.stream.JsonToken jsonToken = reader.peek();
                if (jsonToken == com.google.gson.stream.JsonToken.NULL) {
                    reader.skipValue();
                    continue;
                }

                if (jsonToken == com.google.gson.stream.JsonToken.BEGIN_ARRAY) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        object.add(mValueTypeAdapter.read(reader));
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }

            reader.endObject();

            T[] result = this.mObjectCreator.construct(object.size());
            return object.toArray(result);
        }

    }

    public static final class PrimitiveIntegerArrayAdapter {
        public static void write(JsonWriter writer, int[] value) throws IOException {
            if (null != value) {
                writer.beginArray();
                for (int item : value) {
                    writer.value(item);
                }
                writer.endArray();
            }
        }

        public static int[] read(JsonReader reader) throws IOException {
            ArrayList<Integer> integerArrayList = INTEGER_ARRAY_LIST_ADAPTER.read(reader);
            int[] result = null;
            if (null != integerArrayList) {
                result = new int[integerArrayList.size()];
                for (int idx = 0; idx < integerArrayList.size(); idx++) {
                    result[idx] = integerArrayList.get(idx);
                }
            }
            return result;
        }
    }

    public static final class PrimitiveLongArrayAdapter {
        public static void write(JsonWriter writer, long[] value) throws IOException {
            if (null != value) {
                writer.beginArray();
                for (long item : value) {
                    writer.value(item);
                }
                writer.endArray();
            }
        }

        public static long[] read(JsonReader reader) throws IOException {
            ArrayList<Long> longArrayList = LONG_ARRAY_LIST_ADAPTER.read(reader);
            long[] result = null;
            if (null != longArrayList) {
                result = new long[longArrayList.size()];
                for (int idx = 0; idx < longArrayList.size(); idx++) {
                    result[idx] = longArrayList.get(idx);
                }
            }
            return result;
        }
    }

    public static final class PrimitiveDoubleArrayAdapter {
        public static void write(JsonWriter writer, double[] value) throws IOException {
            if (null != value) {
                writer.beginArray();
                for (double item : value) {
                    writer.value(item);
                }
                writer.endArray();
            }
        }

        public static double[] read(JsonReader reader) throws IOException {
            ArrayList<Double> longArrayList = DOUBLE_ARRAY_LIST_ADAPTER.read(reader);
            double[] result = null;
            if (null != longArrayList) {
                result = new double[longArrayList.size()];
                for (int idx = 0; idx < longArrayList.size(); idx++) {
                    result[idx] = longArrayList.get(idx);
                }
            }
            return result;
        }
    }

    public static final class PrimitiveShortArrayAdapter {
        public static void write(JsonWriter writer, short[] value) throws IOException {
            if (null != value) {
                writer.beginArray();
                for (short item : value) {
                    writer.value(item);
                }
                writer.endArray();
            }
        }

        public static short[] read(JsonReader reader) throws IOException {
            ArrayList<Short> longArrayList = SHORT_ARRAY_LIST_ADAPTER.read(reader);
            short[] result = null;
            if (null != longArrayList) {
                result = new short[longArrayList.size()];
                for (int idx = 0; idx < longArrayList.size(); idx++) {
                    result[idx] = longArrayList.get(idx);
                }
            }
            return result;
        }
    }

    public static class ListInstantiater<V> implements ObjectConstructor<List<V>> {

        @Override
        public List<V> construct() {
            return new ArrayList<V>();
        }
    }

    public static class CollectionInstantiater<V> implements ObjectConstructor<Collection<V>> {

        @Override
        public Collection<V> construct() {
            return new ArrayList<V>();
        }
    }

    public static class ArrayListInstantiater<V> implements ObjectConstructor<ArrayList<V>> {

        @Override
        public ArrayList<V> construct() {
            return new ArrayList<V>();
        }
    }

    public static class HashMapInstantiater<K, V> implements ObjectConstructor<HashMap<K, V>> {

        @Override
        public HashMap<K, V> construct() {
            return new HashMap<K, V>();
        }
    }

    public static class ConcurrentHashMapInstantiater<K, V> implements ObjectConstructor<ConcurrentHashMap<K, V>> {

        @Override
        public ConcurrentHashMap<K, V> construct() {
            return new ConcurrentHashMap<K, V>();
        }
    }

    public static class LinkedHashMapInstantiater<K, V> implements ObjectConstructor<LinkedHashMap<K, V>> {

        @Override
        public LinkedHashMap<K, V> construct() {
            return new LinkedHashMap<K, V>();
        }
    }

    public static class MapInstantiater<K, V> implements ObjectConstructor<Map<K, V>> {

        @Override
        public Map<K, V> construct() {
            return new LinkedHashMap<K, V>();
        }
    }

    public static class ListTypeAdapter<V, T extends Collection<V>> extends TypeAdapter<T> {

        private TypeAdapter<V> valueTypeAdapter;
        private ObjectConstructor<T> objectConstructor;

        public ListTypeAdapter(TypeAdapter<V> valueTypeAdapter, ObjectConstructor<T> objectConstructor) {
            this.valueTypeAdapter = valueTypeAdapter;
            this.objectConstructor = objectConstructor;
        }

        @Override
        public void write(JsonWriter writer, T value) throws IOException {
            writer.beginArray();
            for (V item : value) {
                valueTypeAdapter.write(writer, item);
            }
            writer.endArray();
        }

        @Override
        public T read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }

            T collection = objectConstructor.construct();
            reader.beginArray();
            while (reader.hasNext()) {
                collection.add(valueTypeAdapter.read(reader));
            }
            reader.endArray();
            return collection;
        }
    }

    public static class MapTypeAdapter<K, V, T extends Map<K, V>> extends TypeAdapter<T> {
        private ObjectConstructor<T> objectConstructor;
        private TypeAdapter<V> valueTypeAdapter;
        private TypeAdapter<K> keyTypeAdapter;

        public MapTypeAdapter(TypeAdapter<K> keyTypeAdapter, TypeAdapter<V> valueTypeAdapter, ObjectConstructor<T> objectConstructor) {
            this.keyTypeAdapter = keyTypeAdapter;
            this.valueTypeAdapter = valueTypeAdapter;
            this.objectConstructor = objectConstructor;
        }

        @Override
        public void write(JsonWriter writer, T value) throws IOException {
            boolean hasComplexKeys = false;
            List<JsonElement> keys = new ArrayList<>(value.size());

            List<V> values = new ArrayList<>(value.size());
            for (T.Entry<K, V> entry : value.entrySet()) {
                JsonElement keyElement = keyTypeAdapter.toJsonTree(entry.getKey());
                keys.add(keyElement);
                values.add(entry.getValue());
                hasComplexKeys |= keyElement.isJsonArray() || keyElement.isJsonObject();
            }

            if (hasComplexKeys) {
                writer.beginArray();
                for (int i = 0; i < keys.size(); i++) {
                    writer.beginArray(); // entry array
                    Streams.write(keys.get(i), writer);
                    valueTypeAdapter.write(writer, values.get(i));
                    writer.endArray();
                }
                writer.endArray();
            } else {
                writer.beginObject();
                for (int i = 0; i < keys.size(); i++) {
                    JsonElement keyElement = keys.get(i);
                    writer.name(keyToString(keyElement));
                    valueTypeAdapter.write(writer, values.get(i));
                }
                writer.endObject();
            }
        }

        @Override
        public T read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            T map = objectConstructor.construct();

            if (peek == JsonToken.BEGIN_ARRAY) {
                in.beginArray();
                while (in.hasNext()) {
                    in.beginArray(); // entry array
                    K key = keyTypeAdapter.read(in);
                    V value = valueTypeAdapter.read(in);
                    V replaced = map.put(key, value);
                    if (replaced != null) {
                        throw new JsonSyntaxException("duplicate key: " + key);
                    }
                    in.endArray();
                }
                in.endArray();
            } else {
                in.beginObject();
                while (in.hasNext()) {
                    JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
                    K key = keyTypeAdapter.read(in);
                    V value = valueTypeAdapter.read(in);
                    V replaced = map.put(key, value);
                    if (replaced != null) {
                        throw new JsonSyntaxException("duplicate key: " + key);
                    }
                }
                in.endObject();
            }
            return map;
        }

        private String keyToString(JsonElement keyElement) {
            if (keyElement.isJsonPrimitive()) {
                JsonPrimitive primitive = keyElement.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    return String.valueOf(primitive.getAsNumber());
                } else if (primitive.isBoolean()) {
                    return Boolean.toString(primitive.getAsBoolean());
                } else if (primitive.isString()) {
                    return primitive.getAsString();
                } else {
                    throw new AssertionError();
                }
            } else if (keyElement.isJsonNull()) {
                return "null";
            } else {
                throw new AssertionError();
            }
        }
    }

    private static final class DateTypeAdapter extends TypeAdapter<Date> {
        private final DateFormat enUsFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
        private final DateFormat localFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);

        @Override
        public Date read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return deserializeToDate(in.nextString());
        }

        private synchronized Date deserializeToDate(String json) {
            try {
                return localFormat.parse(json);
            } catch (ParseException ignored) {
            }
            try {
                return enUsFormat.parse(json);
            } catch (ParseException ignored) {
            }
            try {
                return ISO8601Utils.parse(json, new ParsePosition(0));
            } catch (ParseException e) {
                throw new JsonSyntaxException(json, e);
            }
        }

        @Override
        public synchronized void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            String dateFormatAsString = enUsFormat.format(value);
            out.value(dateFormatAsString);
        }
    }

    public static final class DATE_TYPE_ADAPTER {
        static DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();

        public static void write(JsonWriter writer, Date value) throws IOException {
            dateTypeAdapter.write(writer, value);
        }

        public static Date read(JsonReader reader) throws IOException {
            return dateTypeAdapter.read(reader);
        }
    }

    public static final class ObjectTypeAdapter extends TypeAdapter<Object> {

        private final Gson gson;

        public ObjectTypeAdapter(Gson gson) {
            this.gson = gson;
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<Object>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedTreeMap<String, Object>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;

                case STRING:
                    return in.nextString();

                case NUMBER:
                    return in.nextDouble();

                case BOOLEAN:
                    return in.nextBoolean();

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            TypeAdapter<Object> typeAdapter = (TypeAdapter<Object>) gson.getAdapter(value.getClass());
            if (typeAdapter instanceof ObjectTypeAdapter) {
                out.beginObject();
                out.endObject();
                return;
            }

            typeAdapter.write(out, value);
        }
    }

    public static final class JsonObjectTypeAdapter extends TypeAdapter<JsonObject> {

        @Override
        public void write(JsonWriter out, JsonObject value) throws IOException {
            JsonElementTypeAdapter.writeJsonElement(out, value);
        }

        @Override
        public JsonObject read(JsonReader in) throws IOException {
            JsonElement jsonElement = JsonElementTypeAdapter.readJsonElement(in);
            if(null != jsonElement && !jsonElement.isJsonObject()) {
                throw new IOException("Could not parse it as a JsonObject");
            }
            return null != jsonElement && jsonElement.isJsonArray() ? jsonElement.getAsJsonObject() : null;
        }
    }

    public static final class JsonArrayTypeAdapter extends TypeAdapter<JsonArray> {

        @Override
        public void write(JsonWriter out, JsonArray value) throws IOException {
            JsonElementTypeAdapter.writeJsonElement(out, value);
        }

        @Override
        public JsonArray read(JsonReader in) throws IOException {
            JsonElement jsonElement = JsonElementTypeAdapter.readJsonElement(in);
            if(null != jsonElement && !jsonElement.isJsonArray()) {
                throw new IOException("Could not parse it as a JsonArray");
            }
            return null != jsonElement && jsonElement.isJsonArray() ? jsonElement.getAsJsonArray() : null;
        }
    }

    public static final class JsonElementTypeAdapter extends TypeAdapter<JsonElement> {

        public static JsonElement readJsonElement(JsonReader in) throws IOException {
            switch (in.peek()) {
                case STRING:
                    return new JsonPrimitive(in.nextString());
                case NUMBER:
                    String number = in.nextString();
                    return new JsonPrimitive(new LazilyParsedNumber(number));
                case BOOLEAN:
                    return new JsonPrimitive(in.nextBoolean());
                case NULL:
                    in.nextNull();
                    return JsonNull.INSTANCE;
                case BEGIN_ARRAY:
                    JsonArray array = new JsonArray();
                    in.beginArray();
                    while (in.hasNext()) {
                        array.add(readJsonElement(in));
                    }
                    in.endArray();
                    return array;
                case BEGIN_OBJECT:
                    JsonObject object = new JsonObject();
                    in.beginObject();
                    while (in.hasNext()) {
                        object.add(in.nextName(), readJsonElement(in));
                    }
                    in.endObject();
                    return object;
                case END_DOCUMENT:
                case NAME:
                case END_OBJECT:
                case END_ARRAY:
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public JsonElement read(JsonReader in) throws IOException {
           return JsonElementTypeAdapter.readJsonElement(in);
        }


        public static void writeJsonElement(JsonWriter out, JsonElement value) throws IOException {
            if (value == null || value.isJsonNull()) {
                out.nullValue();
            } else if (value.isJsonPrimitive()) {
                JsonPrimitive primitive = value.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    out.value(primitive.getAsNumber());
                } else if (primitive.isBoolean()) {
                    out.value(primitive.getAsBoolean());
                } else {
                    out.value(primitive.getAsString());
                }

            } else if (value.isJsonArray()) {
                out.beginArray();
                for (JsonElement e : value.getAsJsonArray()) {
                    writeJsonElement(out, e);
                }
                out.endArray();

            } else if (value.isJsonObject()) {
                out.beginObject();
                for (Map.Entry<String, JsonElement> e : value.getAsJsonObject().entrySet()) {
                    out.name(e.getKey());
                    writeJsonElement(out, e.getValue());
                }
                out.endObject();

            } else {
                throw new IllegalArgumentException("Couldn't write " + value.getClass());
            }
        }


        @Override
        public void write(JsonWriter out, JsonElement value) throws IOException {
            writeJsonElement(out, value);
        }
    }
}