package Helper;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.Formula;
import com.avaje.ebean.cache.ServerCacheManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.Application;
import controllers.SuperBase;
import models.Vld;
import models.Message;
import play.api.templates.Html;
import play.data.Form;
import play.db.ebean.EbeanPlugin;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.annotation.meta.field;

import javax.persistence.*;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@MappedSuperclass
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties
public class BaseModel extends Model {


    @Transient
    private String formName = "System full update form";

    @Transient
    private String deleteValue = "/";

    @Transient
    private String approveLink = "/";

    @Transient
    private String approveTitle = "/";

    @JsonIgnore
    @Transient
    private String badge = "bg-green";

    @JsonIgnore
    @Transient
    public Result transactionError = Controller.ok("1");

    protected static boolean needAttendance = false;
    protected static int attendanceLevel = 100;
    protected static long examId = 0;


    private static String hashKey = "_~?9";
    private static String toBeReplaced = ".";

    public static String reverse(String s){
        s = s.replace(toBeReplaced,hashKey);
        return new StringBuilder(s).reverse().toString();
    }

    public static String inverse(String s){
        s = new StringBuilder(s).reverse().toString();
        return s.replace(hashKey,toBeReplaced);
    }

    protected static String[] statusListStudent() {
        return ClassFinder.statusListStudent;
    }

    public static void setExamId(Long id){
        examId = id;
    }

    public static boolean isEntity(Class<?> clazz){
        return clazz.isAnnotationPresent(Entity.class);
    }

    public static void setAttendanceLevel(int a){
        attendanceLevel = a;
    }

    public static void setNeedAttendance(){
        needAttendance = true;
    }

    public static void notNeedAttendance(){
        needAttendance = false;
    }

    protected void setDeleteValue(String deleteValue) {
        this.deleteValue = deleteValue;
    }

    public BaseModel warn() {
        this.badge = "bg-yellow";
        return this;
    }

    public BaseModel danger() {
        this.badge = "bg-red";
        return this;
    }

    public BaseModel setLink(String approveLink) {
        this.approveLink = approveLink;
        return this;
    }

    @Override
    public void update() {
        try {
            if (checkExist()) {
                transactionError = Controller.ok("duplicate ");
            } else {
                super.update();
            }
        } catch (Exception e) {
            boolean bool = e.getClass() == OptimisticLockException.class;

            String message = bool ? "Message.badEntity" : e.getMessage();

            transactionError = Controller.ok(message);
        }
    }


    @Override
    public void update(Object id) {
        try {
            if (checkExist()) {
                transactionError = Controller.ok("Duplicate error");
            } else {
                super.update(id);
            }
        } catch (Exception e) {
            boolean bool = e.getClass() == OptimisticLockException.class;

            String message = bool ? "bad entity" : e.getMessage();

            transactionError = Controller.ok(message);
        }
    }

    private boolean checkExist() {
        try {
            for (Method method : this.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Exist.class)) {
                    method.setAccessible(true);
                    Object invoke = method.invoke(this);
                    Exist exist = method.getAnnotation(Exist.class);
                    if (!exist.error().equals("")) transactionError = Controller.ok(exist.error());
                    boolean b = invoke.getClass() == Boolean.class;
                    return b && (boolean) invoke;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            transactionError = Controller.ok(e.getMessage() + " " + e.getClass().getName());
        }
        return false;
    }


    @Override
    public void save() {
        try {
            if (checkExist()) {
                transactionError = Controller.ok("Message.duplicateError");
            } else {
                super.save();
            }
        } catch (Exception e) {
            transactionError = Controller.ok(e.getMessage());
        }
    }

    public BaseModel setTitle(String approveTitle) {
        this.approveTitle = approveTitle;
        return this;
    }

    public BaseModel info() {
        this.badge = "bg-info";
        return this;
    }

    public BaseModel success() {
        this.badge = "bg-green";
        return this;
    }

    public static List<Date> dateList(Date date, Date end) {
        List<Date> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int i=0;
        while (cal.getTime().before(end)) {
            if( i == 0 ) dates.add(cal.getTime());
            cal.add(Calendar.DATE, 1);
            dates.add(cal.getTime());
            i++;
        }

        return dates;
    }

    public String badge(Object o) {
        String s = "<span class=\"badge " + this.badge + "\">" + o.toString() + "</span>";
        this.success();
        return s;
    }

    public void setTools(String name, boolean bool) {
        this.formName = name;
    }

    public void setTools(String name) {
        this.formName = name;
    }

    protected static int limit() {
        return Vld.limit;
    }

    private static String pattern = "EEEE , dd-MMMM-yyyy";
    private static SimpleDateFormat format = new SimpleDateFormat(pattern);
    private static Calendar calendar = Calendar.getInstance();
    private static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

    protected static NumberFormat formatInstance = NumberFormat.getNumberInstance(Locale.US);

    protected String fileSize(double bytes) {
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        double gigabytes = (megabytes / 1024);
        double terabytes = (gigabytes / 1024);
        double petabytes = (terabytes / 1024);
        double exabytes = (petabytes / 1024);
        double zettaBytes = (exabytes / 1024);
        double yottaBytes = (zettaBytes / 1024);

        return bytes <= 1024 ? bytes + " B" : kilobytes <= 1024 ? kilobytes + " KB" : megabytes <= 1024 ? megabytes + " MB" : gigabytes + " GB";

    }

    protected Html imagePath(String type, String at, boolean isImage) {
        boolean isWord = type.equalsIgnoreCase("doc") || type.equalsIgnoreCase("docx");
        boolean isExcel = type.equalsIgnoreCase("xls") || type.equalsIgnoreCase("xlsx");
        boolean isPdf = type.equalsIgnoreCase("pdf") || type.equalsIgnoreCase("pdfx");
        boolean isZip = type.equalsIgnoreCase("zip") || type.equalsIgnoreCase("rar");
        boolean isAudio = type.equalsIgnoreCase("mp3") || type.equalsIgnoreCase("mp2") || type.equals("wav");

        String string = isImage ? "<img src=\"" + SuperBase.realPath(at) + "\">" : isWord ? "<i class=\"fa fa-file-word-o\"></i>" : isExcel ? "<i class=\"fa fa-file-excel-o\"></i>" : isPdf ? "<i class=\"fa fa-file-pdf-o\"></i>" : isZip ? "<i class=\"fa fa-file-pdf-o\"></i>" : isAudio ? "<i class=\"fa fa-file-sound-o\"></i>" : "<i class=\"fa fa-file-text\"></i>";

        return Html.apply(string);
    }

    public static String format(Date date) {
        return format.format(date);
    }

    public static String format(Date date, String pattern) {
        format.applyPattern(pattern);
        String f = format.format(date);
        format.applyPattern(BaseModel.pattern);
        return f;
    }

    public static String day(Date date) {
        return format(date, "EEEE");
    }

    public static String daySm(Date date) {
        return format(date, "EE");
    }

    public static int dayN(Date date) {
        calendar.setTime(date);
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        return i - 1;
    }

    public static int dayOfYear(Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static String year(Date date) {
        return format(date, "yyyy");
    }

    public static String month(Date date) {
        return format(date, "MMMM");
    }

    public static String defaultFormat(Date date) {
        return format2.format(date);
    }

    public static Date parseDef(String date) {
        try {
            return format2.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @JsonIgnore
    public String button() {
        return "<button class=\"btn btn-sm btn-default d-form\" value=\"" + this.formName + "\"><i class=\"fa fa-edit\"></i></button>";
    }

    @JsonIgnore
    protected static String activateButton(String bg, String route, String val) {
        return "<button class=\"btn btn-sm " + bg + "\" onclick=\"activator(this)\" value=\"" + route + "\"><i class=\"fa fa-edit\"></i> " + val + "</button>";
    }

    @JsonIgnore
    protected String addRole() {
        return "<button class=\"btn btn-sm btn-default\" onclick=\"return createModal(this , event);\" href=\"" + this.formName + "\"><i class=\"fa fa-user-plus\"></i> Add role</button>";
    }

    @JsonIgnore
    public String approve() {
        return "<a class=\"btn btn-sm btn-default\" onclick=\"return alertBefore(this , '" + this.approveTitle + "');\" href=\"" + this.approveLink + "\"><i class=\"fa fa-user-plus\"></i> Approve</a>";
    }

    static String getMoreLink(String can){
        return "ssd";
    }

    protected static List<JsonNode> nodeList() {
        List<JsonNode> nodeList = new ArrayList<>();
        ObjectNode node1 = Json.newObject();
        node1.put("id", true);
        node1.put("print", "Module Owner");
        ObjectNode node2 = Json.newObject();
        node2.put("id", false);
        node2.put("print", "Not module Owner");

        nodeList.add(node1);
        nodeList.add(node2);

        return nodeList;
    }

    protected static List<JsonNode> genderList() {
        List<JsonNode> nodeList = new ArrayList<>();
        ObjectNode node1 = Json.newObject();
        node1.put("id", "MALE");
        node1.put("print", "MALE");
        ObjectNode node2 = Json.newObject();
        node2.put("id", "FEMALE");
        node2.put("print", "FEMALE");

        nodeList.add(node1);
        nodeList.add(node2);

        return nodeList;
    }

    @JsonIgnore
    public String deleteButton() {
        return "<button class=\"btn btn-sm delete-btn btn-danger\" value=\"" + this.deleteValue + "\"><i class=\"fa fa-trash\"></i></button>";
    }

    @JsonIgnore
    private static Object getId(Object object) {
        try {
            return object.getClass().getDeclaredField("id").get(object);
        } catch (Exception e) {
            return 0;
        }
    }

    private static Long getLongId(Object o) {
        try {
            return Long.parseLong(getId(o).toString());
        } catch (Exception e) {
            return 0L;
        }
    }

    public static int counter(int a) {
        return Vld.counter(a);
    }


    @JsonIgnore
    public static JsonNode json(Object objectList) {
        return Json.toJson(objectList);
    }


    protected String disableColor(boolean b) {
        return b ? "bg-gray" : "";
    }


    public static class Look<T> {

        private final Class<T> type;
        private final String serverName;
        private final String currentProperty;
        private ExpressionList<T> list = null;
        private Expression expression = null;
        private Expression pageExp = null;
        private Long currentId = null;
        private String saveRoute = "/";
        private String buttonName = "Save Change";
        private String formTitle = "Save form";
        private List<String> columns = new ArrayList<>();
        private List<String[]> columnsAll = new ArrayList<>();
        private List<Class<?>> subClasses = new ArrayList<>();
        private List<Class<?>> subClassesOneToOne = new ArrayList<>();
        private String ctrlName = "Default";
        private List<String> excepted = new ArrayList<>();
        private List<String> disabled = new ArrayList<>();
        private String formDisable = "";
        private String targetQueryId = null;
        private JsonNode targetNode = null;
        private JsonNode fieldsJson = null;
        private List<T> currentList = null;
        private Class<?> formType;
        private List<Class<?>> cList = new ArrayList<>();
        private Query<?> reportQuery = null;
        private boolean isNewDisabled = false;
        private boolean escapeDisableStatus = false;
        private Object[] sortFields = new Object[0];
        private Object[] sortTableFields = new Object[0];
        private Field[] fields = new Field[0];
        private Method[] methods = new Method[0];

        private String orderKey = "id";
        private String sqlDone;


        public Look(Class<T> type) {
            this.type = type;
            this.formType = type;
            this.serverName = "default";
            this.currentProperty = idColumn();
            this.defaultValueSql();
        }

        public Look<T> setSaveRoute(String saveRoute) {
            this.saveRoute = saveRoute;
            return this;
        }

        public Look<T> setTitle(String title) {
            this.formTitle = title;
            return this;
        }
        private void defaultValueSql(){
            this.sqlDone = "No query Executed yet";
        }

        public Look<T> setFormType(Class<?> formType) {
            this.formType = formType;
            return this;
        }

        public String getSqlQuery(){
            return sqlDone;
        }

        public Look<T> setCurrentList(List<T> currentList) {
            this.currentList = currentList;
            return this;
        }

        public Look<T> escapeDisabled() {
            this.escapeDisableStatus = true;
            return this;
        }


        public Look<T> setOrderKey(String orderKey) {
            this.orderKey = orderKey;
            return this;
        }

        public Look<T> setTarget(String targetQueryId) {
            this.targetQueryId = targetQueryId;
            return this;
        }

        public Look<T> disableNew() {
            this.isNewDisabled = true;
            return this;
        }

        public Look<T> setTargetNode(JsonNode node) {
            this.targetNode = node;
            return this;
        }

        public Look<T> setButtonName(String buttonName) {
            this.buttonName = buttonName;
            return this;
        }

        public Look<T> setPageExp(Expression pageExp) {
            this.pageExp = pageExp;
            return this;
        }

        public Look<T> setExpLst(ExpressionList<T> list) {
            this.list = list;
            return this;
        }

        private EbeanServer server() {
            return Ebean.getServer(serverName);
        }

        private Query<T> f() {
            return server().find(type);
        }

        public List<T> list() {
            return this.order(queryT());
        }

        public ExpressionList<T> query() {
            return f().where();
        }

        public T obj(Long id) {
            _setId(id);
            return server().find(type, id);
        }

        public T single() {
            return queryT().setMaxRows(1).findUnique();
        }

        public List<T> all() {
            return order(queryT());
        }

        private List<T> iAll(int a) {
            Query<T> tQuery = this.setPagination(queryT(), a);
            this.reset();
            return this.order(tQuery);
        }

        public List<T> all(int a) {
            return iAll(a);
        }


        private boolean existSingle(String cl, Object val) {
            return query().eq(cl, val).findRowCount() > 0;
        }

        private void existSingleList(String cl, Object val) {
            createQuery();
            list.add(Expr.eq(cl, val));
        }

        public ObjectNode createEl(String type, Object value, String display, String name) {
            return createEl(type, value, display, name, false);
        }

        public ObjectNode createEl(String type, Object value, String display, String name, boolean isNumber) {
            ObjectNode node = Json.newObject();
            node.put("type", type);
            node.put("value", value.toString());
            node.put("name", name);
            if (isNumber) node.put("isNumber", isNumber);
            ObjectNode finalObject = Json.newObject();
            finalObject.set(display, node);
            return finalObject;
        }

        public ObjectNode createNode(String type, Object value, String name, boolean isNumber) {
            ObjectNode node = Json.newObject();
            node.put("type", type);
            node.put("value", value.toString());
            node.put("name", name);
            if (isNumber) node.put("isNumber", true);
            return node;
        }

        public ObjectNode createEl(String type, Object value, String display) {
            return createEl(type, value, display, display);
        }

        public ObjectNode createEl() {
            return createEl("text", "", "Form text");
        }


        public boolean exist(String cl, Object val) {
            return isCurrentIdNull() ? existSingle(cl, val) : existWithId(cl, val);
        }

        public Look<T> existList(String cl, Object val) {
            existSingleList(cl, val);
            return this;
        }

        public boolean executeExist() {
            boolean b = f().where().addAll(checkedId()).findRowCount() > 0;
            reset();
            return b;
        }

        public Look<T> reset() {
            this.list = null;
            this.expression = null;
            this.pageExp = null;
            this.escapeDisableStatus = false;
            this.currentId = null;
            this.reportQuery = null;
            return this;
        }

        public Look<T> enable(String str) {
            this.excepted.add(str);
            return this;
        }

        public Look<T> disable(String str) {
            this.disabled.add(str);
            return this;
        }

        public Look<T> disableFormKey(String str) {
            this.formDisable = str;
            return this;
        }


        private ExpressionList<T> checkedId() {
            if (bothAllowed()) {
                return list.ne(currentProperty, currentId);
            }
            return list;
        }

        public T object() {
            T unique = f().where().addAll(checkedId()).setMaxRows(1).findUnique();
            reset();
            return unique;
        }

        private void _setId(Long id) {
            this.currentId = id;
        }

        private boolean isCurrentIdNull() {
            return this.currentId == null;
        }

        private boolean isCurrentPropNull() {
            return this.currentProperty == null;
        }

        private boolean bothAllowed() {
            return !isCurrentPropNull() && !isCurrentIdNull();
        }

        private boolean existWithId(String cl, Object val) {
            try {
                int i = query().ne(currentProperty, currentId).eq(cl, val).findRowCount();
                this.reset();
                return i > 0;
            } catch (Exception e) {
                return false;
            }
        }

        public JsonNode allColumns() {
            if (columns.isEmpty()) this.setColumns();

            return Json.toJson(columns);
        }

        public T formData() {
            Form<T> form = Form.form(type).bindFromRequest();

            return formData(form);
        }

        public T formData(Long id) {
            _setId(id);
            return formData();
        }

        private T formData(Form<T> form) {
            boolean hasErrors = form.hasErrors();
            T t = hasErrors ? form.get() : server().createEntityBean(type);
            for (Field field : this.getFields()) {

                if( isId(field) ) continue;

                boolean attr = isAttrEntity(field);
                String fieldName = field.getName();
                String value = form.field(fieldName).value();
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                boolean hasForm = field.isAnnotationPresent(FormProp.class);
                boolean capital = hasForm && field.getAnnotation(FormProp.class).Aa();

                try {
                    if (attr) {
                        String idColumn = idColumn(fieldType);

                        if (idColumn == null) continue;

                        String col = fieldName + "." + idColumn;
                        value = form.field(col).value();
                        if (isNumeric(value)) {
                            Object o = server().find(fieldType, Long.parseLong(value));
                            field.set(t, o);
                        }
                    } else if (value != null && isAttr(field)) {
                        Object val = value;
                        boolean isInt = fieldType == int.class || Integer.class == fieldType;
                        boolean isD = fieldType == double.class || Double.class == fieldType;
                        val = isInt ? Integer.valueOf(value) : val;
                        val = isD ? Double.valueOf(value) : val;
                        val = fieldType == Date.class ? format2.parse(value) : val;

                        field.set(t, val);
                    }

                    if (capital && fieldType == String.class) {
                        value = value != null ? value.toUpperCase() : "";
                        field.set(t, value);
                    }
                } catch (IllegalAccessException | ParseException | IllegalArgumentException i) {
                    System.out.println(i.getMessage());
                }
            }
            return t;
        }

        private boolean isNumeric(String s) {
            try {
                Double.parseDouble(s);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private Long getLong(String s) {
            try {
                return Long.parseLong(s);
            } catch (Exception e) {
                return null;
            }
        }

        private String idColumn() {
            if (isCurrentPropNull()) {
                return idColumn(type);
            } else return currentProperty;
        }

        private String idColumn(Class<?> type) {
            for (Field f : type.getDeclaredFields()) {
                if (isId(f)) {
                    return f.getName();
                }
            }
            return null;
        }

        private static boolean isId(Field f) {
            return f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(EmbeddedId.class);
        }

        public Look<T> isSearch(String query) {

            if (columns.isEmpty()) this.setColumns();

            for (String s : columns) {
                this.sChain(s, query);
            }
            return this;
        }

        private void setColumns() {
            Class<T> type = this.type;
            this.tName(type);
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

        private static boolean isAttrEntity(Field field) {
            return isAttr(field) && isEntity(field);
        }

        private static boolean isEntity(Field field) {
            return BaseModel.isEntity(field.getType());
        }

        private boolean isManyToOne(Field field){
            return field.isAnnotationPresent(ManyToOne.class);
        }

        private boolean isOneToOne(Field field){
            return field.isAnnotationPresent(OneToOne.class);
        }

        private void tName(Class<?> type) {
            Field[] declaredFields = getFields(type);
            for (Field field : declaredFields) {
                boolean isEntity = isAttrEntity(field);
                boolean isAttr = isAttr(field);
                boolean isAttribute = isAttr && isForSearch(field);
                String name = field.getName();
                if (isEntity) {
                    tableName(field, name, name);
                    Class<?> fieldType = field.getType();
                    if (isManyToOne(field)) {
                        String[] e = {name, name, fieldType.getName(), idColumn(fieldType)};
                        columnsAll.add(e);
                    }
                    if( doesNotExist(fieldType,subClasses) ) {
                        subClasses.add(fieldType);
                    }

                    if( isOneToOne(field) && notChainDisabled(field) ){
                        subClassesOneToOne.add(fieldType);
                    }
                } else if (isAttribute) {
                    columns.add(name);
                }

            }
        }

        private boolean isForSearch(Field field){
            return !field.getType().isAnnotationPresent(Entity.class) && (field.isAnnotationPresent(TableProp.class) || field.isAnnotationPresent(JsonProperty.class) || (field.isAnnotationPresent(Formula.class) && (field.getType() == String.class)));
        }

        private List<String[]> getAllColumns(){
            if( columnsAll.isEmpty() ) this.setColumns();
            return columnsAll;
        }

        public boolean inColList(String s){
            for (String[] d : getAllColumns() ){
                if( d.length < 4 ) continue;

                if( d[0].equals(s) ) return true;
            }

            return false;
        }

        public void inQuery(String canonicalName,String pkg){
            whereAm(pkg,canonicalName);
        }

        public String getColWithId(String s){
            for (String[] d : getAllColumns() ){
                if( d.length < 4 ) continue;

                if( d[0].equals(s) && d[3] != null ) return s + "." + d[3];
            }
            return null;
        }

        public boolean inColInverseList(String s){
            return inColList(inverse(s));
        }

        private List<Class<?>> whereAm(String pkg){
            return whereAm(pkg,null);
        }

        private List<Class<?>> whereAm(String pkg,String cName){
            List<Class<?>> list = ClassFinder.find(pkg);
            List<Class<?>> classList = whereAm(list,type,null,cName);
            cList = new ArrayList<>();
            List<Class<?>> types = ofOneToOneSubTypes(list);
            //List<Class<?>> addIfNot = addIfNot(classList, types);
            return classList;
        }

        private List<Class<?>> whereAm(List<Class<?>> list,Class<?> type,Query<?> expression,String canonName){
            List<Class<?>> classList = new ArrayList<>();
            for (Class<?> aClass : list ){
                if( amIn(aClass,type) && BaseModel.isEntity(aClass) && doesNotExist(aClass,cList) ){
                    classList.add(aClass);
                    cList.add(aClass);
                    Query<?> query = Ebean.find(aClass);

                    String f = amInString(aClass,type) + "."+idColumn(aClass);

                    query = query.select(f);

                    if( expression != null ) {
                        query = expression.where().in(idColumn(type), query).query();
                    }

                    if( canonName != null && !canonName.isEmpty() && canonName.equals(aClass.getName()) ){
                        this.reportQuery = query;
                        return classList;
                    }

                    classList.addAll(whereAm(list,aClass,query,canonName));


                }
            }
            List<Class<?>> types = ofOneToOneSubTypes(list);
            return classList;
        }

        private List<Class<?>> addIfNot(List<Class<?>> classList,List<Class<?>> toBeAdded){
            List<Class<?>> subClasses = getSubClasses();
            for (Class<?> aClass : toBeAdded){
                if( doesNotExist(aClass,classList) && doesNotExist(aClass,subClasses) && aClass != type ) classList.add(aClass);
            }
            return classList;
        }

        private boolean doesNotExist(Class<?> aClass,List<Class<?>> classList){
            for (Class<?> clazz : classList){
                if( clazz == aClass ) return false;
            }
            return true;
        }

        private List<Class<?>> getSubClasses(){
            if( subClasses.isEmpty() ) this.setColumns();

            return subClasses;
        }

        private List<Class<?>> getSubClassesOneToOne(){
            if( subClassesOneToOne.isEmpty() ) this.setColumns();

            return subClassesOneToOne;
        }

        private List<Class<?>> ofSubTypes(List<Class<?>> classes){
            return ofTypeGiven(classes,getSubClasses());
        }

        private List<Class<?>> ofOneToOneSubTypes(List<Class<?>> classes){
            return ofTypeGiven(classes,getSubClassesOneToOne());
        }

        private List<Class<?>> ofTypeGiven(List<Class<?>> classes,List<Class<?>> aClasses){
            List<Class<?>> classList = new ArrayList<>();
            for (Class<?> aClass : aClasses ){

                for (Class<?> clazz : classes ){
                    if( amIn(clazz,aClass) ) classList.add(clazz);
                }

            }
            return classList;
        }

        private boolean amIn(Class<?> aClass,Class<?> type){
            return amInString(aClass,type) != null;
        }

        private String amInString(Class<?> aClass,Class<?> type){
            if( aClass == type ) return null;

            Field[] fields = getFields(aClass);
            for (Field field : fields){
                if( field.getType() == type && isAttr(field) ) return field.getName();
            }
            return null;
        }

        private boolean amIn(Class<?> aClass){
            return amIn(aClass,type);
        }

        public JsonNode getModalCols(){
            return _colsModal(type,null);
        }

        private ObjectNode _colsModal(Class<?> type, String s2){
            ObjectNode newObject = Json.newObject();

            for (Field field : type.getDeclaredFields() ){

                String name = field.getName();
                final Class<?> fType = field.getType();

                boolean present = field.isAnnotationPresent(ReportHelper.class);

                boolean entity = isAttrEntity(field);
                final boolean isDate = fType == Date.class;
                if( entity || isDate || present ){
                    boolean toOne = isManyToOne(field) || isDate || present;
                    String s = s2;
                    ReportHelper annotation = field.getAnnotation(ReportHelper.class);
                    name = present ? annotation.name() : name;
                    s2 = s2 != null ? s2 + "." + name : name;

                    boolean b = present && BaseModel.isEntity(annotation.clazz());

                    String typeName = b ? annotation.clazz().getName() : fType.getName();


                    ObjectNode node = _colsModal(fType, s2);
                    node.put("value",name);
                    node.put("key",reverse(s2));
                    node.put("store",typeName);
                    node.put("access",toOne);

                    boolean extra = present && !BaseModel.isEntity(annotation.clazz());

                    if(extra) node.put("extra",Arrays.toString(annotation.arrayStatus()));

                    newObject.set(name,node);
                    s2 = s;
                }
            }

            return newObject;
        }

        public List<JsonNode> getAllColumnsHashed(){
            List<JsonNode> nodeList = new ArrayList<>();
            for (String[] string : getAllColumns() ){

                if( string.length < 4 ) continue;

                String s = reverse(string[0]);
                ObjectNode node = Json.newObject();
                node.put("key",s);
                node.put("value",string[1]);
                node.put("store",string[2]);
                nodeList.add(node);
            }

            return nodeList;
        }

        public JsonNode getWhereAmNode(String pkg){
            List<Class<?>> classList = whereAm(pkg);

            List<JsonNode> nodeList = new ArrayList<>();

            for (Class<?> clazz : classList ){

                ObjectNode node = Json.newObject();
                String name = clazz.getSimpleName();
                node.put("key", name);
                node.put("value", name);
                node.put("store", clazz.getName());
                nodeList.add(node);
            }

            return json(nodeList);
        }

        private void tableName(Field f, String s, String s2) {
            Field[] declaredFields = f.getType().getDeclaredFields();
            int i = 0;
            for (Field field : declaredFields) {


                boolean attr = isAttr(field);


                if ( attr ) {
                    if (i != 0) {
                        s = s2;
                    } else {
                        s2 = s;
                    }

                    boolean present = isForSearch(field);

                    final String string = s + "." + field.getName();
                    if( present ) {
                        columns.add(string);
                    }

                    boolean isEntity = isEntity(field);

                    final Class<?> fieldType = field.getType();
                    if( isEntity && isManyToOne(field) ){
                        String[] strings = {string, field.getName(), fieldType.getName(),idColumn(fieldType)};
                        columnsAll.add(strings);
                    }

                    if( isEntity ){
                        subClasses.add(fieldType);
                    }

                    if( isEntity && isOneToOne(field) && notChainDisabled(field) ){
                        subClassesOneToOne.add(fieldType);
                    }


                    if (isEntity) {
                        s = s + "." + field.getName();
                        tableName(field, s, s2);
                    }
                }

                i++;
            }
        }

        private boolean notChainDisabled(Field field){
            boolean present = field.isAnnotationPresent(DisableChain.class);
            if( !present ) return true;
            else{
                DisableChain annotation = field.getAnnotation(DisableChain.class);
                return annotation.aClass() != type;
            }

        }

        public int t(int a) {
            return Vld.t(a);
        }

        private void createQuery() {
            if (list == null) list = query();
        }


        private void sChain(String col, String val) {
            if (expression == null) expression = Expr.icontains(col, val.trim());
            else expression = Expr.or(expression, Expr.icontains(col, val.trim()));
        }

        private Query<T> queryT() {
            ExpressionList<T> tQuery = f().where();
            if (pageExp != null) tQuery.add(pageExp);
            if (list != null) tQuery.addAll(list);
            if (expression != null) tQuery = tQuery.add(expression);

            if( reportQuery != null ) tQuery = tQuery.in(idColumn(),reportQuery);

            reset();
            return tQuery.query();
        }

        private Query<T> setPagination(Query<T> query, int a) {
            return query.setFirstRow(t(a)).setMaxRows(Vld.limit);
        }

        public List<T> search(int a) {
            Query<T> tQuery = this.setPagination(queryT(), a);
            this.reset();
            return this.order(tQuery);
        }

        private String key() {
            return orderKey != null ? orderKey : currentProperty;
        }

        public List<T> order(Query<T> t) {
            String key = key();
            List<T> list;
            t =  t.order(key + " desc");

            list = t.findList();
            this.sqlDone = t.getGeneratedSql();
            return list;
        }

        public List<T> order(ExpressionList<T> t) {
            return this.order(t.query());
        }

        public int searchCount() {
            createQuery();
            Query<T> tQuery = this.queryT();
            int integer = tQuery.findRowCount();
            this.reset();
            return Vld.counter(integer);
        }

        public int number() {
            return queryT().findRowCount();
        }

        public int count() {
            return this.searchCount();
        }

        public Look<T> isLog() {
            this.ctrlName = "Logistic";
            return this;
        }

        public Look<T> isAdmin() {
            this.ctrlName = "Admin";
            return this;
        }

        public Look<T> isReg() {
            this.ctrlName = "Reg";
            return this;
        }

        public Look<T> isApp() {
            this.ctrlName = "App";
            return this;
        }

        public Look<T> isHr() {
            this.ctrlName = "Hr";
            return this;
        }

        public Look<T> isEng() {
            this.ctrlName = "English";
            return this;
        }

        public Look<T> isCFO() {
            this.ctrlName = "CFO";
            return this;
        }

        public Look<T> isFin() {
            this.ctrlName = "Finance";
            return this;
        }

        public Look<T> isHoUnit() {
            this.ctrlName = "HeadOfUnit";
            return this;
        }

        public Look<T> isHOD() {
            this.ctrlName = "department";
            return this;
        }


        public Look<T> isDaf() {
            this.ctrlName = "DAF";
            return this;
        }

        public Look<T> isSuper() {
            this.ctrlName = "Super";
            return this;
        }

        public Look<T> isDean() {
            this.ctrlName = "Dean";
            return this;
        }

        public Look<T> isLib() {
            this.ctrlName = "Library";
            return this;
        }

        public Look<T> isActive() {
            this.ctrlName = "ActiveUser";
            return this;
        }

        public JsonNode countNode(String t) {
            List<JsonNode> list = new ArrayList<>();
            int count = count();
            for (int i = 1; i <= count; i++) {
                ObjectNode node = Json.newObject();
                node.put("number", i);
                node.put("route", "");
                list.add(node);
            }
            this.ctrlName = "Default";
            return Json.toJson(list);
        }

        public JsonNode countNode(String t, String query) {
            List<JsonNode> list = new ArrayList<>();
            int count = isSearch(query).searchCount();
            for (int i = 1; i <= count; i++) {
                ObjectNode node = Json.newObject();
                node.put("number", i);
                node.put("route", "");
                list.add(node);
            }
            this.ctrlName = "Default";
            return Json.toJson(list);
        }

        public JsonNode nodeList(int a) {
            return Json.toJson(all(a));
        }

        public JsonNode nodeList() {
            return Json.toJson(all());
        }

        public JsonNode _structNodeList() {
            ObjectNode node = Json.newObject();
            node.set("data", nodeList());
            node.set("fields", _structure());
            node.set("header", SuperBase.headerInfo());

            return node;
        }

        private JsonNode _struct(Class<?> type) {
            ObjectNode node = Json.newObject();
            String display;
            boolean isAvailable = false;
            for (Method method : type.getDeclaredMethods()) {
                String name = method.getName();
                display = name;

                if (method.isAnnotationPresent(TableProp.class)) {
                    TableProp annotation = method.getAnnotation(TableProp.class);
                    display = annotation.reportName().equals("") ? display : annotation.reportName();

                    isAvailable = annotation.isReport();
                } else isAvailable = false;

                if (method.isAnnotationPresent(FormProp.class)) {
                    FormProp annotation = method.getAnnotation(FormProp.class);
                    display = annotation.reportName().equals("") ? display : annotation.reportName();

                    isAvailable = annotation.isReport() || isAvailable;
                }

                isAvailable = isAvailable || method.isAnnotationPresent(JsonProperty.class);


                Class<?> returnType = method.getReturnType();


                if (!isAvailable) continue;

                boolean attr = isMethodJson(method);
                if (attr && BaseModel.isEntity(returnType) ) {
                    node.set(name, _struct(returnType));
                } else if (attr) {
                    node.put(name, display);
                }

            }
            for (Field field : type.getDeclaredFields()) {
                String name = field.getName();
                display = name;

                if( field.isAnnotationPresent(JsonProperty.class) ){
                    JsonProperty property = field.getAnnotation(JsonProperty.class);
                    name = property.value().equals("") ? name : property.value();
                }

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
                } else if (attr) {
                    node.put(name, display);
                }
            }
            return node;
        }

        private static boolean isAttrJson(Field field) {
            return isAttr(field) && !field.isAnnotationPresent(JsonIgnore.class) && Modifier.isPublic(field.getModifiers()) && !isId(field);
        }

        private static boolean isMethodJson(Method method) {
            return isMethod(method) && Modifier.isPublic(method.getModifiers()) && !method.isAnnotationPresent(NoJsonReport.class);
        }

        private JsonNode _structure() {
            JsonNode node = fieldsJson != null ? fieldsJson : this._struct(type);
            fieldsJson = node;
            return node;
        }

        public JsonNode page() {
            return page(1);
        }

        public boolean inArray(String needle) {
            return inArray(needle, this.excepted);
        }

        private boolean inArray(String needle, List<String> stringList) {

            boolean valid = false;
            for (String i : stringList) {
                if (needle.equals(i)) {
                    valid = true;
                    break;
                }
            }
            return valid;
        }

        private boolean inDisabled(String needle) {
            return inArray(needle, this.disabled);
        }

        public JsonNode page(int a) {
            ObjectNode node = Json.newObject();
            final List<T> all = this.currentList != null ? this.currentList : this.search(a);

            this.currentList = null;

            Object[] declaredMethods = this.sortTable(getMethods(), getFields(), true);
            int inc = 0;
            List<ObjectNode> nodeList = new ArrayList<>();
            for (T t : all) {
                ObjectNode oNode = Json.newObject();
                for (Object method : declaredMethods) {
                    Class<?> aClass = method.getClass();
                    Object value = "";
                    String name = "";
                    boolean annotationPresent = false;
                    TableProp annotation = null;
                    try {
                        if (aClass == Method.class) {
                            Method method1 = (Method) method;
                            value = method1.invoke(t);
                            name = method1.getName();
                            annotationPresent = method1.isAnnotationPresent(TableProp.class);
                            annotation = method1.getAnnotation(TableProp.class);
                        } else if (aClass == Field.class) {
                            Field field = (Field) method;
                            value = field.get(t);
                            name = field.getName();
                            annotationPresent = field.isAnnotationPresent(TableProp.class);
                            annotation = field.getAnnotation(TableProp.class);
                        }
                        if (value == null) value = "";
                    } catch (Exception ignored) {
                        value = "x";
                    }
                    if (annotationPresent && annotation != null) {

                        ObjectNode objectNode = Json.newObject();
                        objectNode.put("name", annotation.name());


                        boolean bld = isDismissed(name, annotation);

                        putTableAttr(objectNode, value, annotation);
                        String f = annotation.display().equals(".") ? name : annotation.display();

                        if (bld) oNode.set(f, objectNode);
                    }
                }


                nodeList.add(oNode);
                inc++;
            }

            node.set("form", form());

            node.set("page", json(nodeList));


            if (isNewDisabled) {
                node.put("newDisabled", true);
            }
            this.isNewDisabled = false;

            this.excepted = new ArrayList<>();
            this.disabled = new ArrayList<>();

            return node;

        }

        private boolean isDismissed(String methodName, TableProp annotation) {
            boolean bld;

            boolean disable = !inDisabled(methodName);

            bld = annotation.dismissed() ? inArray(methodName) && disable : disable;

            return bld;
        }

        private void putTableAttr(ObjectNode objectNode, Object value, TableProp annotation) {
            if (annotation.isNode()) {
                objectNode.put("value", Json.toJson(value));
            } else
                objectNode.put("value", value.toString());

            if (annotation.isHtml()) {
                objectNode.put("isHtml", true);
            }

            if (annotation.isForm()) {
                objectNode.put("isForm", true);
            }

            if (annotation.isButton()) {
                objectNode.put("button", true);
            }
        }

        private JsonNode formHead() {
            ObjectNode jsonNodes = Json.newObject();

            String fName = "formName";
            String route = "saveRoute";

            jsonNodes.put(route, saveRoute);


            boolean annotationPresent = type.isAnnotationPresent(FormProp.class);
            String name;
            if (annotationPresent) {
                FormProp annotation = type.getAnnotation(FormProp.class);
                name = annotation.formName().equals(".") ? type.getSimpleName() : annotation.formName();
            } else {
                name = this.formTitle;
            }

            jsonNodes.put(fName, name);
            String buttonName = "buttonName";

            jsonNodes.put(buttonName, this.buttonName);

            return jsonNodes;
        }

        public JsonNode form(Long o) {
            _setId(o);
            JsonNode form = form();
            reset();
            return form;
        }

        private String putAttr(ObjectNode node, FormProp annotation, String name, String idColumn) {
            String f = annotation.name();
            idColumn = idColumn == null ? f : "." + idColumn;
            String formName = annotation.isRel() && f.equals(".id") ? name + idColumn : f.equals(".id") ? name : f;
            String attributeName = annotation.attribute();
            String className = annotation.className();
            String idName = annotation.id();
            String typeName = annotation.type();
            String displayName = annotation.display().equals(".") ? name : annotation.display();


            node.put("name", formName);
            node.put("type", typeName);
            node.put("attr", attributeName);
            node.put("className", className);
            node.put("id", idName);

            if (annotation.isCal()) {
                node.put("isCalendar", true);
            }

            if (annotation.isTime()) {
                node.put("timePicker", true);
            }

            if (!annotation.isNaN()) {
                node.put("isNumber", true);
            }

            if (annotation.isCheck()) {
                node.put("isCheck", true);
            }

            if (annotation.isGroup()) {
                node.put("grouped", true);
            }

            if (annotation.escape()) {
                node.put("escape", true);
            }

            if (annotation.isDownload()) {
                node.put("isDownload", true);
            }

            if (annotation.isDisabled()) {
                node.put("disabled", true);
            }

            return displayName;
        }


        private Object[] sortTable(Method[] methods, Field[] fields, boolean isTable) {

            boolean is = formType == type;

            if (this.sortFields.length > 0 && !isTable && is) return this.sortFields;
            else if (this.sortTableFields.length > 0 && isTable && is) return this.sortTableFields;

            int i, p, x, ac;
            int len = methods.length;
            List<Object> methodList = new ArrayList<>();
            for (Method method : methods) {
                boolean annotationPresent = method.isAnnotationPresent(TableProp.class);
                boolean present = method.isAnnotationPresent(FormProp.class);
                if (annotationPresent && isTable) {
                    methodList.add(method);
                } else if (present && !isTable) {
                    methodList.add(method);
                }
            }
            for (Field field : fields) {
                boolean annotationPresent = field.isAnnotationPresent(TableProp.class);
                boolean present = field.isAnnotationPresent(FormProp.class);
                if (annotationPresent && isTable) {
                    methodList.add(field);
                } else if (present && !isTable) {
                    methodList.add(field);
                }
            }
            Object[] objects = methodList.toArray(new Object[0]);
            for (int a = 0; a < objects.length; a++) {
                if (a != 0) {
                    for (p = 0; p <= a; p++) {
                        for (ac = a; ac > p; ac--) {

                            boolean isField = objects[ac].getClass() == Field.class;
                            boolean isOldField = objects[ac - 1].getClass() == Field.class;
                            boolean isMethod = objects[ac].getClass() == Method.class;
                            boolean isOldMethod = objects[ac - 1].getClass() == Method.class;

                            TableProp formProp = null;
                            TableProp fProp = null;

                            FormProp form = null;
                            FormProp form2 = null;

                            if (isField && isTable) {
                                Field field = (Field) objects[ac];
                                formProp = field.getAnnotation(TableProp.class);
                            } else if (isField) {
                                Field field = (Field) objects[ac];
                                form = field.getAnnotation(FormProp.class);
                            }

                            if (isOldField && isTable) {
                                Field field = (Field) objects[ac - 1];
                                fProp = field.getAnnotation(TableProp.class);
                            } else if (isOldField) {
                                Field field = (Field) objects[ac - 1];
                                form2 = field.getAnnotation(FormProp.class);
                            }

                            if (isMethod && isTable) {
                                Method method = (Method) objects[ac];
                                formProp = method.getAnnotation(TableProp.class);
                            } else if (isMethod) {
                                Method method = (Method) objects[ac];
                                form = method.getAnnotation(FormProp.class);
                            }

                            if (isOldMethod && isTable) {
                                Method method = (Method) objects[ac - 1];
                                fProp = method.getAnnotation(TableProp.class);
                            } else if (isOldMethod) {
                                Method method = (Method) objects[ac - 1];
                                form2 = method.getAnnotation(FormProp.class);
                            }

                            int order1;
                            int order2;

                            if (formProp != null && fProp != null) {
                                order1 = formProp.order();
                                order2 = fProp.order();
                            } else if (form != null && form2 != null) {
                                order1 = form.order();
                                order2 = form2.order();
                            } else continue;

                            if (order1 < order2) {
                                Object nex = objects[ac];
                                objects[ac] = objects[ac - 1];
                                objects[ac - 1] = nex;
                            }
                        }
                    }
                }
            }

            if (!isTable && is) this.sortFields = objects;
            else if (is) this.sortTableFields = objects;

            return objects;
        }

        private Field[] getFields(Class<?> type) {
            boolean greater = fields.length < 1;
            boolean isType = this.type == type;
            boolean b = greater && isType;

            if (b) {
                fields = type.getDeclaredFields();
                return fields;
            }

            if (!isType) {
                return type.getDeclaredFields();
            }
            return fields;
        }

        private Method[] getMethods(Class<?> type) {
            boolean greater = methods.length < 1;
            boolean isType = this.type == type;
            boolean b = greater && isType;

            if (b) {
                methods = type.getDeclaredMethods();
                return methods;
            }

            if (!isType) {
                return type.getDeclaredMethods();
            }
            return methods;
        }

        private Field[] getFields() {
            return getFields(type);
        }

        private Method[] getMethods() {
            return getMethods(type);
        }


        public JsonNode form() {
            Method[] declaredMethods = getMethods(formType);
            Field[] declaredFields = getFields(formType);
            ObjectNode jsonNodes = Json.newObject();

            ObjectNode childNode = Json.newObject();

            final Object objectT = isCurrentIdNull() ? null : server().find(type, currentId);
            final Object newObjectT = server().createEntityBean(type);


            Object[] objects = this.sortTable(declaredMethods, declaredFields, false);

            FormProp annotation;
            boolean isPresent;
            String name, idColumn;
            Class<?> fieldType;

            for (Object object : objects) {
                ObjectNode node = Json.newObject();

                Object ob;
                if (object.getClass() == Field.class) {
                    Field field = (Field) object;
                    try {
                        field.setAccessible(true);
                        boolean aStatic = Modifier.isStatic(field.getModifiers());
                        Object dV = aStatic ? field.get(newObjectT) : null;
                        Object o = isCurrentIdNull() ? dV : field.get(objectT);
                        if (o != null) {
                            ob = o;
                            node.put("defaultValue", getLongId(o));
                        } else ob = "";

                    } catch (Exception e) {
                        ob = "";
                    }
                    isPresent = field.isAnnotationPresent(FormProp.class);
                    annotation = field.getAnnotation(FormProp.class);
                    fieldType = field.getType();
                    name = field.getName();
                    idColumn = idColumn(fieldType);
                } else if (object.getClass() == Method.class) {
                    Method method = (Method) object;
                    try {
                        method.setAccessible(true);
                        boolean aStatic = Modifier.isStatic(method.getModifiers());
                        Object dV = aStatic ? method.invoke(newObjectT) : null;
                        Object o = isCurrentIdNull() ? dV : method.invoke(objectT);
                        if (o != null) {
                            node.put("defaultValue", getLongId(o));
                            ob = o;
                        } else ob = "";

                    } catch (Exception e) {
                        ob = "";
                    }
                    isPresent = method.isAnnotationPresent(FormProp.class);
                    annotation = method.getAnnotation(FormProp.class);
                    fieldType = method.getReturnType();
                    name = method.getName();
                    idColumn = idColumn(fieldType);
                } else continue;

                if (isPresent) {

                    String string = putAttr(node, annotation, name, idColumn);
                    createMissingNode(fieldType, node, ob, annotation);

                    if (!name.equals(this.formDisable)) {
                        childNode.set(string, node);
                    }
                }
            }

            String fData = "formData";
            String fHead = "formHead";
            jsonNodes.set(fData, childNode);
            jsonNodes.set(fHead, formHead());

            this.formType = this.type;
            this.formDisable = "";

            return jsonNodes;
        }

        private void createMissingNode(Class<?> type, ObjectNode node, Object o, FormProp formProp) {
            boolean annotation = BaseModel.isEntity(type);
            if (annotation) {
                ExpressionList<?> list = server().find(type).where();
                boolean condition = !formProp.eqProp().equals(".") && !formProp.eqValue().equals(".");

                if (condition) list.add(Expr.eq(formProp.eqProp(), formProp.eqValue()));
                if (this.targetQueryId != null && this.targetNode != null && formProp.id().equals(this.targetQueryId)) {
                    node.set("value", this.targetNode);
                    this.targetNode = null;
                    this.targetQueryId = null;
                } else {
                    Object listList;
                    int rowCount = list.findRowCount();
                    if( rowCount > 100 ){
                        node.put("search",getMoreLink(type.getName()));
                        listList = list.setMaxRows(100).findList();
                    }else listList = list.findList();
                    if (!formProp.newNull().equals("")) {
                        ObjectNode newNode = Json.newObject();
                        newNode.put("print", formProp.newNull());
                        newNode.put("id", 0);
                        ((List) listList).add(newNode);
                    }
                    JsonNode jsonNode = Json.toJson(listList);
                    node.set("value", jsonNode);
                }
            } else {
                if (type.equals(Date.class)) {
                    String format;
                    try {
                        format = defaultFormat((Date) o);
                    } catch (Exception e) {
                        format = "";
                    }
                    node.put("value", format);
                } else if (formProp.isNode()) {
                    node.set("value", Json.toJson(o));
                } else {
                    node.put("value", o.toString());
                }
            }
        }
    }


}
