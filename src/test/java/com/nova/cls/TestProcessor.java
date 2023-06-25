package com.nova.cls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nova.cls.data.Command;
import com.nova.cls.data.Processor;
import com.nova.cls.data.Response;
import com.nova.cls.data.models.Good;
import com.nova.cls.data.models.Group;
import com.nova.cls.data.models.OffsetGoodQuantity;
import com.nova.cls.data.services.DatabaseHandler;
import com.nova.cls.data.services.criteria.goods.GoodsCriteriaAggregate;
import com.nova.cls.data.services.criteria.groups.GroupsCriteriaAggregate;
import com.nova.cls.network.packets.Message;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class TestProcessor {
    private final static JsonMapper mapper = new JsonMapper();
    private static Processor processor;
    private Group vegetables;
    private Good carrot;

    @BeforeClass
    public static void setUpClass() {
        DatabaseHandler.setDbFileUrl("jdbc:sqlite:testData.db");
        DatabaseHandler.initDatabase();
        processor = new Processor();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        try {
            processor.close();
        } catch (Exception e) {
            fail("Unexpected exception " + e.getMessage());
            e.printStackTrace();
        }
        Files.delete(Path.of("testData.db"));
        DatabaseHandler.setDbFileUrl("jdbc:sqlite:data.db");
        DatabaseHandler.initDatabase(); // re-init, this time in actual data.db
    }

    @Before
    public void setUp() throws JsonProcessingException {
        vegetables = new Group();
        vegetables.setGroupName("Овочі");
        vegetables.setDescription("Свіжа продукція");
        Message response = processor.process(
            new Message(Command.GROUPS_CREATE, 0,
                mapper.writeValueAsString(vegetables)));
        vegetables.setGroupId(Integer.parseInt(response.getBody()));

        carrot = new Good();
        carrot.setGoodName("Морква");
        carrot.setDescription("Якісна");
        carrot.setManufacturer("Дідусів город");
        carrot.setPrice(5000);
        carrot.setGroupId(vegetables.getGroupId());
        response = processor.process(new Message(Command.GOODS_CREATE, 0,
            mapper.writeValueAsString(carrot)));
        carrot.setGoodId(Integer.parseInt(response.getBody()));
    }

    @After
    public void tearDown() {
        processor.process(
            new Message(Command.GROUPS_DELETE, 0, vegetables.getGroupId()));
    }

    @Test
    public void testRead() throws JsonProcessingException {
        Message request =
            new Message(Command.GROUPS_READ, 0, vegetables.getGroupId());
        Message response = processor.process(request);
        assertEquals(mapper.writeValueAsString(vegetables), response.getBody());
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        assertBadRequest(new Message(Command.GROUPS_READ, 0, "Abracadabra"));

        request = new Message(Command.GOODS_READ, 0, carrot.getGoodId());
        response = processor.process(request);
        assertEquals(mapper.writeValueAsString(carrot), response.getBody());
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        assertBadRequest(new Message(Command.GOODS_READ, 0, "Abracadabra"));
    }

    @Test
    public void testCreate() throws JsonProcessingException {
        Group meat = new Group();
        meat.setGroupName("М'ясо");
        meat.setDescription("Погано для довкілля");
        Message request = new Message(Command.GROUPS_CREATE, 0,
            mapper.writeValueAsString(meat));
        Message response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        int meatId = Integer.parseInt(response.getBody());
        meat.setGroupId(meatId);

        assertBadRequest(request); // duplicate name
        meat.setGroupName(null);
        assertBadRequest(new Message(Command.GROUPS_CREATE, 0,
            mapper.writeValueAsString(meat)));
        meat.setGroupName("Не М'ясо"); // avoid duplicate
        meat.setDescription(null);
        assertBadRequest(new Message(Command.GROUPS_CREATE, 0,
            mapper.writeValueAsString(meat)));
        meat.setDescription("Погано для довкілля");
        assertBadRequest(
            new Message(Command.GROUPS_CREATE, 0, "ab")); // malformed json
        meat.setGroupName("М'ясо"); // return for future comparisons

        request = new Message(Command.GROUPS_READ, 0,
            meatId); // test that actually created
        response = processor.process(request);
        assertEquals(mapper.writeValueAsString(meat), response.getBody());

        Good chicken = new Good();
        chicken.setGoodName("Курка");
        chicken.setDescription("Сира");
        chicken.setManufacturer("Наша Ряба");
        chicken.setPrice(5000);
        chicken.setGroupId(meatId);
        request = new Message(Command.GOODS_CREATE, 0,
            mapper.writeValueAsString(chicken));
        response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        int chickenId = Integer.parseInt(response.getBody());
        chicken.setGoodId(chickenId);

        assertBadRequest(request); // duplicate name
        chicken.setGoodName(null);
        assertBadRequest(new Message(Command.GOODS_CREATE, 0,
            mapper.writeValueAsString(chicken)));
        chicken.setGoodName("Не Курка");
        chicken.setDescription(null);
        assertBadRequest(new Message(Command.GOODS_CREATE, 0,
            mapper.writeValueAsString(chicken)));
        chicken.setDescription("Сира");
        chicken.setManufacturer(null);
        assertBadRequest(new Message(Command.GOODS_CREATE, 0,
            mapper.writeValueAsString(chicken)));
        chicken.setManufacturer("Наша Ряба");
        chicken.setGoodName("Курка"); // return for future comparisons

        request = new Message(Command.GOODS_READ, 0,
            chickenId); // test that actually created
        response = processor.process(request);
        assertEquals(mapper.writeValueAsString(chicken), response.getBody());

        processor.process(
            new Message(Command.GROUPS_DELETE, 0, meatId)); // cleanup
    }

    @Test
    public void testUpdate() throws JsonProcessingException {
        vegetables.setGroupName("Вже не овочі");
        vegetables.setDescription("Точно не овочі");
        Message request = new Message(Command.GROUPS_UPDATE, 0,
            mapper.writeValueAsString(vegetables));
        Message response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        vegetables.setDescription(null);
        assertBadRequest(new Message(Command.GROUPS_UPDATE, 0,
            mapper.writeValueAsString(
                vegetables))); // update refers to the same Group as before
        vegetables.setDescription("Точно не овочі");

        request = new Message(Command.GROUPS_READ, 0,
            vegetables.getGroupId()); // test that actually created
        response = processor.process(request);
        assertEquals(mapper.writeValueAsString(vegetables), response.getBody());

        carrot.setGoodName("Вже не морква");
        carrot.setQuantity(5555);
        carrot.setManufacturer("Садочок");
        request = new Message(Command.GOODS_UPDATE, 0,
            mapper.writeValueAsString(carrot));
        response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        response = processor.process(
            new Message(Command.GOODS_READ, 0, carrot.getGoodId()));
        assertNotEquals(mapper.writeValueAsString(carrot),
            response.getBody()); // quantity should not be 5555, because it is updated via separate explicit commands for adding and removing items
        carrot.setQuantity(0);
        assertEquals(mapper.writeValueAsString(carrot), response.getBody());

        carrot.setManufacturer(null);
        assertBadRequest(new Message(Command.GOODS_UPDATE, 0,
            mapper.writeValueAsString(carrot)));
        carrot.setManufacturer("Садочок");

        request = new Message(Command.GOODS_READ, 0, carrot.getGoodId());
        response = processor.process(request);
        assertEquals(mapper.writeValueAsString(carrot), response.getBody());

        assertEquals(0, mapper.readValue(response.getBody(), Good.class)
            .getQuantity()); // quantity does not update from regular update requests, it is tracked explicitly
        OffsetGoodQuantity offsetGoodQuantity = new OffsetGoodQuantity();
        offsetGoodQuantity.setGoodId(carrot.getGoodId());
        offsetGoodQuantity.setOffset(10);

        response = processor.process(new Message(Command.GOODS_ADD_QUANTITY, 0,
            mapper.writeValueAsString(offsetGoodQuantity)));
        carrot.setQuantity(10);
        assertEquals(Response.OK, Response.get(response.getMessageType()));


        response = processor.process(
            new Message(Command.GOODS_SUBTRACT_QUANTITY, 0,
                mapper.writeValueAsString(offsetGoodQuantity)));
        carrot.setQuantity(0);
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        assertBadRequest(new Message(Command.GOODS_SUBTRACT_QUANTITY, 0,
            mapper.writeValueAsString(offsetGoodQuantity)));
    }

    // starts deletion from the good
    @Test
    public void testDelete() {
        Message request =
            new Message(Command.GOODS_DELETE, 0, carrot.getGoodId());
        Message response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        assertBadRequest(
            new Message(Command.GOODS_READ, 0, carrot.getGoodId()));

        request =
            new Message(Command.GROUPS_DELETE, 0, vegetables.getGroupId());
        response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        assertBadRequest(
            new Message(Command.GROUPS_READ, 0, vegetables.getGroupId()));
    }

    // starts deletion from the group, automatically deleting the good
    @Test
    public void testDeleteGroup() {
        Message request =
            new Message(Command.GROUPS_DELETE, 0, vegetables.getGroupId());
        Message response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        assertBadRequest(
            new Message(Command.GROUPS_READ, 0, vegetables.getGroupId()));
        assertBadRequest(new Message(Command.GOODS_READ, 0,
            carrot.getGoodId())); // deleted in cascade after vegetables
        assertBadRequest(new Message(Command.GROUPS_DELETE, 0, "Овочі"));
        assertBadRequest(new Message(Command.GOODS_DELETE, 0, "Морква"));
    }

    @Test
    public void testList() throws JsonProcessingException {
        assertBadRequest(new Message(Command.GROUPS_LIST, 0, "abracadabra"));

        GroupsCriteriaAggregate groupsCriteriaAggregate =
            new GroupsCriteriaAggregate(true);
        Message request = new Message(Command.GROUPS_LIST, 0,
            mapper.writeValueAsString(groupsCriteriaAggregate));
        Message response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        assertEquals(mapper.writeValueAsString(new Group[] {vegetables}),
            response.getBody());

        groupsCriteriaAggregate = new GroupsCriteriaAggregate(false);
        request = new Message(Command.GROUPS_LIST, 0,
            mapper.writeValueAsString(groupsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        assertEquals("[]", response.getBody());

        // make carrot's quantity non-0
        OffsetGoodQuantity quantity = new OffsetGoodQuantity();
        quantity.setOffset(17);
        quantity.setGoodId(carrot.getGoodId());
        processor.process(new Message(Command.GOODS_ADD_QUANTITY, 0,
            mapper.writeValueAsString(quantity)));
        carrot.setQuantity(17);

        // add tomato
        Good tomato = new Good();
        tomato.setGoodName("Томат");
        tomato.setDescription("To-may-to to-mah-to");
        tomato.setManufacturer("Чумак");
        tomato.setPrice(2000);
        tomato.setGroupId(vegetables.getGroupId());
        response = processor.process(new Message(Command.GOODS_CREATE, 0,
            mapper.writeValueAsString(tomato)));
        int tomatoId = Integer.parseInt(response.getBody());
        tomato.setGoodId(tomatoId);

        assertBadRequest(new Message(Command.GOODS_LIST, 0, "abracadabra"));

        GoodsCriteriaAggregate goodsCriteriaAggregate =
            new GoodsCriteriaAggregate(vegetables.getGroupId(), "Дідусів город",
                null, null, null, null);
        request = new Message(Command.GOODS_LIST, 0,
            mapper.writeValueAsString(goodsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(mapper.writeValueAsString(new Good[] {carrot}),
            response.getBody()); // only carrot is from Дідусів город

        goodsCriteriaAggregate =
            new GoodsCriteriaAggregate(vegetables.getGroupId(), null, null,
                2500, null, null);
        request = new Message(Command.GOODS_LIST, 0,
            mapper.writeValueAsString(goodsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(mapper.writeValueAsString(new Good[] {tomato}),
            response.getBody()); // carrot is 5000 kop, tomato is 2000 kop

        goodsCriteriaAggregate =
            new GoodsCriteriaAggregate(vegetables.getGroupId(), null, null,
                2500, 5, null);
        request = new Message(Command.GOODS_LIST, 0,
            mapper.writeValueAsString(goodsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(mapper.writeValueAsString(new Good[] {}),
            response.getBody()); // tomatoes are not in the list because there are none in stock, and min 5 is specified

        goodsCriteriaAggregate =
            new GoodsCriteriaAggregate(vegetables.getGroupId(), null, 2000,
                null, 5, null);
        request = new Message(Command.GOODS_LIST, 0,
            mapper.writeValueAsString(goodsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(mapper.writeValueAsString(new Good[] {carrot}),
            response.getBody()); // both items are more expensive than 2000 kop, but only carrot has more than 5 units

        // test empty group listing
        processor.process(new Message(Command.GOODS_DELETE, 0,
            carrot.getGoodId()));
        processor.process(new Message(Command.GOODS_DELETE, 0,
            tomato.getGoodId()));
        groupsCriteriaAggregate = new GroupsCriteriaAggregate(false);
        request = new Message(Command.GROUPS_LIST, 0,
            mapper.writeValueAsString(groupsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        assertEquals(mapper.writeValueAsString(new Group[] {vegetables}),
            response.getBody());
    }

    private void assertBadRequest(Message request) {
        Message response = processor.process(request);
        assertEquals(Response.BAD_REQUEST,
            Response.get(response.getMessageType()));
    }
}
