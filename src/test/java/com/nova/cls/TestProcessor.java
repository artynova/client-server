package com.nova.cls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nova.cls.data.Command;
import com.nova.cls.data.Processor;
import com.nova.cls.data.Response;
import com.nova.cls.data.models.Good;
import com.nova.cls.data.models.GoodsJsonMapper;
import com.nova.cls.data.models.Group;
import com.nova.cls.data.models.GroupsJsonMapper;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class TestProcessor {
    private static Processor processor;
    private final GroupsJsonMapper groupsMapper = new GroupsJsonMapper();
    private final GoodsJsonMapper goodsMapper = new GoodsJsonMapper();
    private Group vegetables, fruit;
    private Good carrot, apple;

    // connect to a db in a temporary test database file
    @BeforeClass
    public static void setUpClass() {
        DatabaseHandler.setDbFileUrl("jdbc:sqlite:testData.db");
        DatabaseHandler.initDatabase();
        processor = new Processor();
    }

    // disconnect from db, delete temporary file and switch to actual data.db
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
        DatabaseHandler.initDatabase();
    }

    // add example groups and goods
    @Before
    public void setUp() throws JsonProcessingException {
        vegetables = new Group();
        vegetables.setGroupName("Овочі");
        vegetables.setDescription("Свіжа продукція");
        Message response =
            processor.process(new Message(Command.GROUPS_CREATE, 0, groupsMapper.toCreateJson(vegetables)));
        vegetables.setGroupId(Long.parseLong(response.getBody()));

        fruit = new Group();
        fruit.setGroupName("Фрукти");
        fruit.setDescription("Дуже стиглі");
        response = processor.process(new Message(Command.GROUPS_CREATE, 0, groupsMapper.toCreateJson(fruit)));
        fruit.setGroupId(Long.parseLong(response.getBody()));


        carrot = new Good();
        carrot.setGoodName("Морква");
        carrot.setDescription("Якісна");
        carrot.setManufacturer("Дідусів город");
        carrot.setPrice(5000L);
        carrot.setGroupId(vegetables.getGroupId());
        response = processor.process(new Message(Command.GOODS_CREATE, 0, goodsMapper.toCreateJson(carrot)));
        carrot.setGoodId(Long.parseLong(response.getBody()));

        apple = new Good();
        apple.setGoodName("Яблуко");
        apple.setDescription("Голден");
        apple.setManufacturer("Дідусів сад");
        apple.setPrice(4500L);
        apple.setGroupId(fruit.getGroupId());
        response = processor.process(new Message(Command.GOODS_CREATE, 0, goodsMapper.toCreateJson(apple)));
        apple.setGoodId(Long.parseLong(response.getBody()));
    }

    // delete example groups, and the contained goods along with them
    @After
    public void tearDown() {
        processor.process(new Message(Command.GROUPS_DELETE, 0, vegetables.getGroupId()));
        processor.process(new Message(Command.GROUPS_DELETE, 0, fruit.getGroupId()));

    }

    @Test
    public void testRead() throws IOException {
        // request to get the vegetables group by its id
        Message request = new Message(Command.GROUPS_READ, 0, vegetables.getGroupId());
        Message response = processor.process(request);
        assertEquals(vegetables, groupsMapper.fromReadJson(response.getBody()));
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        // invalid body
        assertBadRequest(new Message(Command.GROUPS_READ, 0, "Abracadabra"));
        // nonexistent group
        assertBadRequest(new Message(Command.GROUPS_READ, 0, 30));

        request = new Message(Command.GOODS_READ, 0, carrot.getGoodId());
        response = processor.process(request);
        assertEquals(carrot, goodsMapper.fromReadJson(response.getBody()));
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        // invalid body
        assertBadRequest(new Message(Command.GOODS_READ, 0, "Abracadabra"));
        // nonexistent good
        assertBadRequest(new Message(Command.GOODS_READ, 0, 30));
    }

    @Test
    public void testCreate() throws IOException {
        Group meat = new Group();

        // bad requests
        // invalid body
        assertBadRequest(new Message(Command.GROUPS_CREATE, 0, "abracadabra"));

        // bad (null) name
        meat.setGroupName(null);
        meat.setDescription("Погано для довкілля");
        assertBadRequest(new Message(Command.GROUPS_CREATE, 0, groupsMapper.toCreateJson(meat)));
        // bad (duplicate) name
        meat.setGroupName("Фрукти");
        assertBadRequest(new Message(Command.GROUPS_CREATE, 0, groupsMapper.toCreateJson(meat)));
        meat.setGroupName("М'ясо");
        // bad (null) description
        meat.setDescription(null);
        assertBadRequest(new Message(Command.GROUPS_CREATE, 0, groupsMapper.toCreateJson(meat)));
        meat.setDescription("Погано для довкілля");

        // success
        Message request = new Message(Command.GROUPS_CREATE, 0, groupsMapper.toCreateJson(meat));
        Message response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        meat.setGroupId(Long.parseLong(response.getBody()));

        // meat exists
        request = new Message(Command.GROUPS_READ, 0, meat.getGroupId());
        response = processor.process(request);
        assertEquals(meat, groupsMapper.fromReadJson(response.getBody()));

        Good chicken = new Good();

        // bad requests
        // invalid body
        assertBadRequest(new Message(Command.GOODS_CREATE, 0, "abracadabra"));

        // bad (null) name
        chicken.setDescription("Сира");
        chicken.setManufacturer("Наша Ряба");
        chicken.setPrice(5000L);
        chicken.setGroupId(meat.getGroupId());
        chicken.setGoodName(null);
        assertBadRequest(new Message(Command.GOODS_CREATE, 0, goodsMapper.toCreateJson(chicken)));
        // bad (null) name
        chicken.setGoodName("Яблуко");
        assertBadRequest(new Message(Command.GOODS_CREATE, 0, goodsMapper.toCreateJson(chicken)));
        chicken.setGoodName("Курка");
        // bad (null) description
        chicken.setDescription(null);
        assertBadRequest(new Message(Command.GOODS_CREATE, 0, goodsMapper.toCreateJson(chicken)));
        chicken.setDescription("Сира");
        // bad (null) manufacturer
        chicken.setManufacturer(null);
        assertBadRequest(new Message(Command.GOODS_CREATE, 0, goodsMapper.toCreateJson(chicken)));
        chicken.setManufacturer("Наша Ряба");
        // bad (negative) price
        chicken.setPrice(-155L);
        assertBadRequest(new Message(Command.GOODS_CREATE, 0, goodsMapper.toCreateJson(chicken)));
        chicken.setPrice(5000L);
        // bad (nonexistent) group
        chicken.setGroupId(30L);
        assertBadRequest(new Message(Command.GOODS_CREATE, 0, goodsMapper.toCreateJson(chicken)));
        chicken.setGroupId(meat.getGroupId());

        // success
        request = new Message(Command.GOODS_CREATE, 0, goodsMapper.toCreateJson(chicken));
        response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        chicken.setGoodId(Long.parseLong(response.getBody()));

        // chicken exists
        request = new Message(Command.GOODS_READ, 0, chicken.getGoodId()); // test that actually created
        response = processor.process(request);
        assertEquals(chicken, goodsMapper.fromReadJson(response.getBody()));

        // clean up meat (and chicken)
        processor.process(new Message(Command.GROUPS_DELETE, 0, meat.getGroupId())); // cleanup
    }

    @Test
    public void testUpdate() throws IOException {
        // update a group
        // bad requests
        // invalid json
        assertBadRequest(new Message(Command.GROUPS_UPDATE, 0, "abracadabra"));

        // bad (nonexistent) id
        vegetables.setGroupId(vegetables.getGroupId() + 10);
        assertBadRequest(new Message(Command.GROUPS_UPDATE, 0, groupsMapper.toUpdateJson(vegetables)));
        vegetables.setGroupId(vegetables.getGroupId() - 10); // go back
        // bad (null) name
        vegetables.setGroupName(null);
        assertBadRequest(new Message(Command.GROUPS_UPDATE, 0, groupsMapper.toUpdateJson(vegetables)));
        // bad (duplicate) name
        vegetables.setGroupName("Фрукти");
        assertBadRequest(new Message(Command.GROUPS_UPDATE, 0, groupsMapper.toUpdateJson(vegetables)));
        vegetables.setGroupName("Вже не овочі");
        // bad (null) description
        vegetables.setDescription(null);
        assertBadRequest(new Message(Command.GROUPS_UPDATE, 0, groupsMapper.toUpdateJson(vegetables)));
        vegetables.setDescription("Точно не овочі");

        // success
        Message request = new Message(Command.GROUPS_UPDATE, 0, groupsMapper.toUpdateJson(vegetables));
        Message response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        // verify vegetables update
        request = new Message(Command.GROUPS_READ, 0, vegetables.getGroupId());
        response = processor.process(request);
        assertEquals(vegetables, groupsMapper.fromReadJson(response.getBody()));

        // update a good
        // bad requests
        // invalid json
        assertBadRequest(new Message(Command.GOODS_UPDATE, 0, "abracadabra"));

        // bad (nonexistent) id
        carrot.setGoodId(carrot.getGoodId() + 10);
        assertBadRequest(new Message(Command.GOODS_UPDATE, 0, goodsMapper.toUpdateJson(carrot)));
        carrot.setGoodId(carrot.getGoodId() - 10); // go back
        // bad (null) name
        carrot.setGoodName(null);
        assertBadRequest(new Message(Command.GOODS_UPDATE, 0, goodsMapper.toUpdateJson(carrot)));
        // bad (duplicate) name
        carrot.setGoodName("Яблуко");
        assertBadRequest(new Message(Command.GOODS_UPDATE, 0, goodsMapper.toUpdateJson(carrot)));
        carrot.setGoodName("Вже не морква");
        // bad (null) description
        carrot.setDescription(null);
        assertBadRequest(new Message(Command.GOODS_UPDATE, 0, goodsMapper.toUpdateJson(carrot)));
        carrot.setDescription("Точно не морква");
        // bad (null) manufacturer
        carrot.setManufacturer(null);
        assertBadRequest(new Message(Command.GOODS_UPDATE, 0, goodsMapper.toUpdateJson(carrot)));
        carrot.setManufacturer("Садочок");
        // bad (non-positive) price
        carrot.setPrice(0L);
        assertBadRequest(new Message(Command.GOODS_UPDATE, 0, goodsMapper.toUpdateJson(carrot)));
        carrot.setPrice(6050L);

        // success
        carrot.setQuantity(5555L); // should not affect the carrot because
        // quantity is updated separately
        carrot.setGroupId(fruit.getGroupId()); // should not affect the
        // carrot because group of a good does not change
        request = new Message(Command.GOODS_UPDATE, 0, goodsMapper.toUpdateJson(carrot));
        response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        // verify carrot update
        response = processor.process(new Message(Command.GOODS_READ, 0, carrot.getGoodId()));
        assertNotEquals(carrot,
            goodsMapper.fromReadJson(response.getBody())); // reasons for inequality are outlined above
        Good responseGood = goodsMapper.fromReadJson(response.getBody());
        assertEquals(0L, responseGood.getQuantity());
        assertEquals(vegetables.getGroupId(), responseGood.getGroupId());

        carrot.setQuantity(0L);
        carrot.setGroupId(vegetables.getGroupId());
        assertEquals(carrot, goodsMapper.fromReadJson(response.getBody()));

        OffsetGoodQuantity offsetGoodQuantity = new OffsetGoodQuantity(carrot.getGoodId(), 10);

        // add 10
        response = processor.process(
            new Message(Command.GOODS_ADD_QUANTITY, 0, goodsMapper.toOffsetQuantityJson(offsetGoodQuantity)));
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        // verify addition
        response = processor.process(new Message(Command.GOODS_READ, 0, carrot.getGoodId()));
        assertEquals(10, goodsMapper.fromReadJson(response.getBody()).getQuantity());

        // subtract 10
        response = processor.process(
            new Message(Command.GOODS_SUBTRACT_QUANTITY, 0, goodsMapper.toOffsetQuantityJson(offsetGoodQuantity)));
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        // verify subtraction
        response = processor.process(new Message(Command.GOODS_READ, 0, carrot.getGoodId()));
        assertEquals(0, goodsMapper.fromReadJson(response.getBody()).getQuantity());

        // subtract below 0
        assertBadRequest(
            new Message(Command.GOODS_SUBTRACT_QUANTITY, 0, goodsMapper.toOffsetQuantityJson(offsetGoodQuantity)));
    }

    // starts deletion from the good
    @Test
    public void testDelete() {
        Message request = new Message(Command.GOODS_DELETE, 0, carrot.getGoodId());
        Message response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        // good no longer exists
        assertBadRequest(new Message(Command.GOODS_READ, 0, carrot.getGoodId()));
        assertBadRequest(new Message(Command.GOODS_DELETE, 0, carrot.getGoodId()));

        request = new Message(Command.GROUPS_DELETE, 0, vegetables.getGroupId());
        response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        // group no longer exists
        assertBadRequest(new Message(Command.GROUPS_READ, 0, vegetables.getGroupId()));
        assertBadRequest(new Message(Command.GROUPS_DELETE, 0, vegetables.getGroupId()));
    }

    // starts deletion from the group, automatically deleting the good
    @Test
    public void testDeleteGroup() {
        Message request = new Message(Command.GROUPS_DELETE, 0, vegetables.getGroupId());
        Message response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));

        // test deleted entities no longer exist
        assertBadRequest(new Message(Command.GROUPS_READ, 0, vegetables.getGroupId()));
        assertBadRequest(new Message(Command.GOODS_READ, 0, carrot.getGoodId())); // deleted in cascade after vegetables
        assertBadRequest(new Message(Command.GROUPS_DELETE, 0, "Овочі"));
        assertBadRequest(new Message(Command.GOODS_DELETE, 0, "Морква"));
    }

    @Test
    public void testList() throws IOException {
        // invalid json
        assertBadRequest(new Message(Command.GROUPS_LIST, 0, "abracadabra"));

        // list groups with goods
        GroupsCriteriaAggregate groupsCriteriaAggregate = new GroupsCriteriaAggregate(true);
        Message request = new Message(Command.GROUPS_LIST, 0, groupsMapper.toCriteriaJson(groupsCriteriaAggregate));
        Message response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        assertEquals(List.of(vegetables, fruit), groupsMapper.fromReadManyJson(response.getBody()));

        // list groups without goods
        groupsCriteriaAggregate = new GroupsCriteriaAggregate(false);
        request = new Message(Command.GROUPS_LIST, 0, groupsMapper.toCriteriaJson(groupsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
//        assertEquals("[]", response.getBody());

        // make carrot's quantity non-0 for further testing
        OffsetGoodQuantity quantity = new OffsetGoodQuantity();
        quantity.setOffset(17);
        quantity.setGoodId(carrot.getGoodId());
        processor.process(new Message(Command.GOODS_ADD_QUANTITY, 0, goodsMapper.toOffsetQuantityJson(quantity)));
        carrot.setQuantity(17);

        // add tomato
        Good tomato = new Good();
        tomato.setGoodName("Томат");
        tomato.setDescription("To-may-to to-mah-to");
        tomato.setManufacturer("Чумак");
        tomato.setPrice(2000);
        tomato.setGroupId(vegetables.getGroupId());
        response = processor.process(new Message(Command.GOODS_CREATE, 0, goodsMapper.toCreateJson(tomato)));
        tomato.setGoodId(Long.parseLong(response.getBody()));

        assertBadRequest(new Message(Command.GOODS_LIST, 0, "abracadabra"));

        // get goods with given group and manufacturer
        GoodsCriteriaAggregate goodsCriteriaAggregate =
            new GoodsCriteriaAggregate(vegetables.getGroupId(), "Дідусів город", null, null, null, null);
        request = new Message(Command.GOODS_LIST, 0, goodsMapper.toCriteriaJson(goodsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(List.of(carrot),
            goodsMapper.fromReadManyJson(response.getBody())); // only carrot is from Дідусів город

        // get goods with given group and max price
        goodsCriteriaAggregate = new GoodsCriteriaAggregate(vegetables.getGroupId(), null, null, 2500L, null, null);
        request = new Message(Command.GOODS_LIST, 0, goodsMapper.toCriteriaJson(goodsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(List.of(tomato),
            goodsMapper.fromReadManyJson(response.getBody())); // carrot is 5000 kop, tomato is 2000 kop

        // get goods with given group, max price and min quantity
        goodsCriteriaAggregate = new GoodsCriteriaAggregate(vegetables.getGroupId(), null, null, 2500L, 5L, null);
        request = new Message(Command.GOODS_LIST, 0, goodsMapper.toCriteriaJson(goodsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(List.of(), goodsMapper.fromReadManyJson(
            response.getBody())); // tomatoes are not in the list because there are none in stock, and min 5 is specified

        // get goods with given group, min price and min quantity
        goodsCriteriaAggregate = new GoodsCriteriaAggregate(vegetables.getGroupId(), null, 2000L, null, 5L, null);
        request = new Message(Command.GOODS_LIST, 0, goodsMapper.toCriteriaJson(goodsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(List.of(carrot), goodsMapper.fromReadManyJson(
            response.getBody())); // both items are more expensive than 2000 kop, but only carrot has more than 5 units

        // list empty groups (this time with a non-empty resulting list)
        processor.process(new Message(Command.GOODS_DELETE, 0, carrot.getGoodId()));
        processor.process(new Message(Command.GOODS_DELETE, 0, tomato.getGoodId()));
        groupsCriteriaAggregate = new GroupsCriteriaAggregate(false);
        request = new Message(Command.GROUPS_LIST, 0, groupsMapper.toCriteriaJson(groupsCriteriaAggregate));
        response = processor.process(request);
        assertEquals(Response.OK, Response.get(response.getMessageType()));
        assertEquals(List.of(vegetables), groupsMapper.fromReadManyJson(response.getBody()));
    }

    private void assertBadRequest(Message request) {
        Message response = processor.process(request);
        assertEquals(Response.BAD_REQUEST, Response.get(response.getMessageType()));
    }
}
