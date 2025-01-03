package models.Utility;
import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Training;
import play.libs.Json;
import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Contracts<T> {
    private final Class<T> iType;
    private List<String> columns;
    private final String serverName;
    private JsonNode fieldsJson = null;
    private ExpressionList<T> list = null;
    private Expression expression = null;
    private Expression pageExp = null;
    private Long currentId = null;
    private String saveRoute = "/";
    private String orderKey = "id";
    private String sqlDone;
    private final String currentProperty;

    public Contracts(Class<T> iType) {
        this.iType = iType;
        this.columns = new ArrayList<>();
        this.serverName = "default";
        this.currentProperty = idColumn();
    }

    public Contracts<T> setColumns(String... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    private boolean isCurrentPropNull() {
        return this.currentProperty == null;
    }
    private String idColumn() {
        if (isCurrentPropNull()) {
            return idColumn(iType);
        } else return currentProperty;
    }

    private String idColumn(Class<?> iType) {
        for (Field f : iType.getDeclaredFields()) {
            if (isId(f)) {
                return f.getName();
            }
        }
        return null;
    }

    public Page<T> page(int page, int pageSize, String search) {
        search = search.trim();
        Expression expression = null;
        for (String column : this.columns) {
            if (expression == null) {
                expression = Expr.icontains(column, search);
            } else {
                expression = Expr.or(expression, Expr.icontains(column, search));
            }
        }
        return queryT().where()
                .add(expression)
                .eq("deleteStatus",false)
                .orderBy("id desc")
                .findPagingList(pageSize)
                .setFetchAhead(false).getPage(page);
    }

    public Page<T> pageByTraining(int page, int pageSize, String search, Training training, String status) {
        search = search.trim();
        Expression expression = null;
        for (String column : this.columns) {
            if (expression == null) {
                expression = Expr.icontains(column, search);
            } else {
                expression = Expr.or(expression, Expr.icontains(column, search));
            }
        }
        ExpressionList<T> expressionList = Ebean.find(iType).where()
                .add(expression)
                .eq("deleteStatus",false)
                .eq("applied.training.id", training.id)
                .eq("applied.applicationStatus", "accepted");
        if (status.equalsIgnoreCase("paid")) {
            expressionList.ge("account.amountPaid", training.minPayment);
        } else if (status.equalsIgnoreCase("unpaid")) {
            expressionList.lt("account.amountPaid", training.minPayment);
        }

        return expressionList.orderBy("id desc")
                .findPagingList(pageSize)
                .setFetchAhead(false).getPage(page);
    }
    public Page<T> paymentsByTraining(int page, int pageSize, String search, Training training) {
        search = search.trim();
        Expression expression = null;
        for (String column : this.columns) {
            if (expression == null) {
                expression = Expr.icontains(column, search);
            } else {
                expression = Expr.or(expression, Expr.icontains(column, search));
            }
        }
        ExpressionList<T> expressionList = Ebean.find(iType).where()
                .add(expression)
                .eq("account.applicant.applied.training.id", training.id);

        return expressionList
                .eq("deleteStatus",false)
                .orderBy("id desc")
                .findPagingList(pageSize)
                .setFetchAhead(false).getPage(page);
    }


    public JsonNode _structNodeList() {
        ObjectNode node = Json.newObject();
        node.set("data", nodeList());
        node.set("fields", _structure());
        node.set("header", Json.newObject());

        return node;
    }

    private EbeanServer server() {
        return Ebean.getServer(serverName);
    }

    private Query<T> f() {
        return server().find(iType);
    }

    public Contracts<T> setPageExp(Expression pageExp) {
        this.pageExp = pageExp;
        return this;
    }

    public Contracts<T> setExpLst(ExpressionList<T> list) {
        this.list = list;
        return this;
    }

    private JsonNode _struct(Class<?> iType) {
        ObjectNode node = Json.newObject();
        String display;
        boolean isAvailable = false;
        for (Method method : iType.getDeclaredMethods()) {
            String name = method.getName();
            display = name;

            if (method.isAnnotationPresent(TableProp.class)) {
                TableProp annotation = method.getAnnotation(TableProp.class);
                display = annotation.reportName().equals("") ? display : annotation.reportName();

                isAvailable = annotation.isReport();
            } else isAvailable = false;


            Class<?> returniType = method.getReturnType();


            if (!isAvailable) continue;

            boolean attr = isMethodJson(method);
            if (attr && returniType.isAnnotationPresent(Entity.class)) {
                node.set(name, _struct(returniType));
            } else if (attr)
                node.put(name, display);

        }
        for (Field field : iType.getDeclaredFields()) {
            String name = field.getName();
            display = name;

            if (field.isAnnotationPresent(TableProp.class)) {
                TableProp annotation = field.getAnnotation(TableProp.class);
                display = annotation.reportName().equals("") ? display : annotation.reportName();
            }

            if (field.isAnnotationPresent(FormProp.class)) {
                FormProp annotation = field.getAnnotation(FormProp.class);
                display = annotation.reportName().equals("") ? display : annotation.reportName();
            }

            Class<?> fieldType = field.getType();
            boolean attr = isAttrJson(field);
            if (attr && isEntity(field)) {
                node.set(name, _struct(fieldType));
            } else if (attr)
                node.put(name, display);
        }
        return node;
    }
    private static boolean isAttrJson(Field field) {
        return isAttr(field) && Modifier.isPublic(field.getModifiers()) && !isId(field);
    }


    private static boolean isId(Field f) {
        return f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(EmbeddedId.class);
    }

    private static boolean isAttrEntity(Field field) {
        return isAttr(field) && isEntity(field);
    }

    private static boolean isEntity(Field field) {
        return field.getType().isAnnotationPresent(Entity.class);
    }

    private static boolean isAttr(Field field) {
        boolean annotationPresent = field.isAnnotationPresent(Transient.class);
        boolean aFinal = Modifier.isFinal(field.getModifiers());
        boolean aStatic = Modifier.isStatic(field.getModifiers());

        boolean hasAnnotation = field.isAnnotationPresent(OneToOne.class);
        boolean equals = false;
        if (hasAnnotation) {
            OneToOne annotation = field.getAnnotation(OneToOne.class);
            equals = !annotation.mappedBy().equals("");
        }

        boolean one = field.isAnnotationPresent(OneToMany.class);
        boolean equals1 = false;
        if (one) {
            OneToMany annotation = field.getAnnotation(OneToMany.class);
            equals1 = !annotation.mappedBy().equals("");
        }

        return !annotationPresent && !aFinal && !aStatic && !equals && !equals1;
    }


    private static boolean isMethod(Method method) {
        boolean annotationPresent = method.isAnnotationPresent(Transient.class);
        boolean aFinal = Modifier.isFinal(method.getModifiers());
        boolean aStatic = Modifier.isStatic(method.getModifiers());
        boolean property = method.isAnnotationPresent(JsonProperty.class);

        return !annotationPresent && !aFinal && !aStatic && property;
    }

    private static boolean isMethodJson(Method method) {
        return isMethod(method) && Modifier.isPublic(method.getModifiers());
    }

    private JsonNode _structure() {
        JsonNode node = fieldsJson != null ? fieldsJson : this._struct(iType);
        fieldsJson = node;
        return node;
    }

    public JsonNode nodeList() {
        return Json.toJson(queryT().findList());
    }

    public List<T> all() {
        return order(queryT());
    }

    public List<T> order(Query<T> t) {
        String key = key();
        List<T> list;
        t = key != null ? t.order(key + " desc") : t;

        list = t.findList();
        this.sqlDone = t.getGeneratedSql();
        return list;
    }
    private String key() {
        return orderKey != null ? orderKey : currentProperty;
    }

    public Contracts<T> reset() {
        this.list = null;
        this.expression = null;
        this.pageExp = null;
        this.currentId = null;
        return this;
    }

    private Query<T> queryT() {
        ExpressionList<T> tQuery = f().where();
        if (pageExp != null) tQuery.add(pageExp);
        if (list != null) tQuery.addAll(list);
        if (expression != null) tQuery = tQuery.add(expression);
        reset();
        return tQuery.query();
    }


}
