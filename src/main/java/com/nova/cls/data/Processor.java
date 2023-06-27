package com.nova.cls.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nova.cls.data.exceptions.request.BadRequestException;
import com.nova.cls.data.models.Good;
import com.nova.cls.data.models.GoodsJsonMapper;
import com.nova.cls.data.models.Group;
import com.nova.cls.data.models.GroupsJsonMapper;
import com.nova.cls.data.models.OffsetGoodQuantity;
import com.nova.cls.data.services.DatabaseHandler;
import com.nova.cls.data.services.GoodsService;
import com.nova.cls.data.services.GroupsService;
import com.nova.cls.data.services.criteria.Criterion;
import com.nova.cls.network.packets.Message;
import com.nova.cls.util.CloseableThreadLocal;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class Processor implements AutoCloseable {
    static {
        DatabaseHandler.initDatabase();
    }

    private final GroupsJsonMapper groupsMapper = new GroupsJsonMapper();
    private final GoodsJsonMapper goodsMapper = new GoodsJsonMapper();
    private final CloseableThreadLocal<Connection> connectionLocal =
        new CloseableThreadLocal<>(DatabaseHandler::getConnection);
    private final CloseableThreadLocal<GroupsService> groupsServiceLocal =
        new CloseableThreadLocal<>(() -> new GroupsService(connectionLocal.get()));
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal =
        new CloseableThreadLocal<>(() -> new GoodsService(connectionLocal.get()));
    private boolean closed = false;

    public Message process(Message request) {
        try {
            return makeResponse(request, Response.OK, makeResponseBody(request));
        } catch (BadRequestException | JsonProcessingException |
            // json processing exception occurs when json payload is malformed, which qualifies as a bad request
            NumberFormatException e) { // NumberFormatException occurs when an id payload is malformed (not an integer string)
            return makeResponse(request, Response.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return makeResponse(request, Response.SERVER_ERROR, e.getMessage());
        }
    }

    private String makeResponseBody(Message request) throws IOException {
        long commandIndex = request.getMessageTypeUnsigned();
        if (commandIndex > Command.values().length) {
            throw new BadRequestException(
                "Command index " + commandIndex + " is too big, max is " + (Command.values().length - 1));
        }

        Command command = Command.get((int) commandIndex);
        String requestBody = request.getBody();

        return switch (command) {
            case GROUPS_CREATE -> createGroup(requestBody);
            case GROUPS_READ -> findOneGroup(requestBody);
            case GROUPS_LIST -> findManyGroups(requestBody);
            case GROUPS_UPDATE -> updateGroup(requestBody);
            case GROUPS_DELETE -> deleteGroup(requestBody);

            case GOODS_CREATE -> createGood(requestBody);
            case GOODS_READ -> findOneGood(requestBody);
            case GOODS_LIST -> findManyGoods(requestBody);
            case GOODS_UPDATE -> updateGood(requestBody);
            case GOODS_ADD_QUANTITY -> addGoodQuantity(requestBody);
            case GOODS_SUBTRACT_QUANTITY -> subtractGoodQuantity(requestBody);
            case GOODS_DELETE -> deleteGood(requestBody);
        };
    }

    private String createGroup(String body) throws IOException {
        long id = groupsServiceLocal.get().create(groupsMapper.fromCreateJson(body));
        return String.valueOf(id); // return id of created group
    }

    private String findOneGroup(String body) throws JsonProcessingException {
        Group result = groupsServiceLocal.get().findOne(Long.parseLong(body));
        return groupsMapper.toReadJson(result);
    }

    private String findManyGroups(String body) throws IOException {
        List<Criterion<Group>> criteria = groupsMapper.fromCriteriaJson(body).criteria();
        List<Group> result = groupsServiceLocal.get().findAll(criteria);
        return groupsMapper.toReadManyJson(result);
    }

    private String updateGroup(String body) throws IOException {
        groupsServiceLocal.get().update(groupsMapper.fromUpdateJson(body));
        return "";
    }

    private String deleteGroup(String body) {
        groupsServiceLocal.get().delete(Long.parseLong(body));
        return "";
    }

    private String createGood(String body) throws IOException {
        long id = goodsServiceLocal.get().create(goodsMapper.fromCreateJson(body));
        return String.valueOf(id); // return id of created good
    }

    private String findOneGood(String body) throws JsonProcessingException {
        Good result = goodsServiceLocal.get().findOne(Long.parseLong(body));
        return goodsMapper.toReadJson(result);
    }

    private String findManyGoods(String body) throws IOException {
        List<Criterion<Good>> criteria = goodsMapper.fromCriteriaJson(body).criteria();
        List<Good> result = goodsServiceLocal.get().findAll(criteria);
        return goodsMapper.toReadManyJson(result);
    }

    private String updateGood(String body) throws IOException {
        goodsServiceLocal.get().update(goodsMapper.fromUpdateJson(body));
        return "";
    }

    private String addGoodQuantity(String body) throws IOException {
        OffsetGoodQuantity quantity = goodsMapper.fromOffsetQuantityJson(body);
        if (quantity.getOffset() < 0) {
            throw new BadRequestException(
                "Cannot add less than 0 goods units (" + quantity.getOffset() + "), use subtraction instead");
        }
        goodsServiceLocal.get().addQuantity(quantity.getGoodId(), quantity.getOffset());
        return "";
    }

    private String subtractGoodQuantity(String body) throws IOException {
        OffsetGoodQuantity quantity = goodsMapper.fromOffsetQuantityJson(body);
        if (quantity.getOffset() < 0) {
            throw new BadRequestException(
                "Cannot subtract less than 0 goods units (" + quantity.getOffset() + "), use addition instead");
        }
        goodsServiceLocal.get().subtractQuantity(quantity.getGoodId(), quantity.getOffset());
        return "";
    }

    private String deleteGood(String body) {
        goodsServiceLocal.get().delete(Long.parseLong(body));
        return "";
    }

    private Message makeResponse(Message request, Response responseType, String body) {
        return new Message(responseType, request.getUserId(), body);
    }

    @Override
    public void close() throws Exception {
        if (isClosed()) {
            return;
        }
        connectionLocal.close();
        groupsServiceLocal.close();
        goodsServiceLocal.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
