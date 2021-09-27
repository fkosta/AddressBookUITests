package qa.issart.com.generators;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class DataGenerator {

    @Parameter(names = "-d", description = "type of generated data", required = true)
    private String dataType;

    @Parameter(names = "-f", description = "file name with test data")
    private String fileName;

    @Parameter(names = "-t", description = "data file type", required = true)
    private String fileType;

    @Parameter(names = "-n", description = "number of records", required = true)
    private int recordsNum;

    @Parameter(names = "-o", description = "random / indexed data")
    private String dataMode = "r";

    private final String filePath = "src/test/resources/";
    private String imagesPath;
    private String imagesType;
    private String[] imagesList;
    private int dn=0;
    private int rsl=0;
    private RandomFactory randomFactory = new RandomFactory();
    private final Properties properties = new Properties();
    private final String projectPath = System.getProperty("user.dir");

    public DataGenerator(String dataType, String dataMode, int recordsNum, String propFileName) throws IOException {
        this.dataType = dataType;
        this.dataMode = dataMode;
        this.recordsNum = recordsNum;
        this.properties.load(new FileReader(filePath+propFileName));
    }

    DataGenerator(){

    }

    public void initParameters(){
        imagesPath = this.properties.getProperty("imagesPath");
        imagesType = this.properties.getProperty("imagesType");
        File imagesFolder = new File(imagesPath);

        if(imagesType.equals("")){
            imagesList = imagesFolder.list();
        }
        else {
            this.imagesList = imagesFolder.list(new FilenameFilter() {
                private Pattern pattern = Pattern.compile(imagesType);

                @Override
                public boolean accept(File dir, String name) {
                    return pattern.matcher(name).matches();
                }
            });
        }
        dn = Integer.parseInt(properties.getProperty("domainsNum"));
        rsl = Integer.parseInt(properties.getProperty("randomStringLength"));
    }

    private String selectImage() {
        return projectPath+"\\"+imagesPath+imagesList[randomFactory.getRInt(imagesList.length)];
    }

    public class Generator<T>{
        public List<T> dataList = new ArrayList<>();
        Class<T> dataClass;
        T dataObject;

        public Generator(Class<T> dataClass){
            this.dataClass = dataClass;
        }

        private T buildDataObject() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            this.dataObject = dataClass.getDeclaredConstructor().newInstance();
            if(dataObject instanceof ContactData){
                ContactData objectData = (ContactData) dataObject;
                objectData.withFirstname(randomFactory.getRString(rsl)).withMiddlename(randomFactory.getRString(rsl))
                        .withLastname(randomFactory.getRString(rsl)).withNickname(randomFactory.getRString(rsl))
                        .withPhoto(selectImage()).withCompany(randomFactory.getRString(rsl))
                        .withTitle(randomFactory.getRString(rsl)).withAddress(randomFactory.getRString(rsl))
                        .withHome(randomFactory.getRStringD(10)).withMobile(randomFactory.getRStringD(10))
                        .withWork(randomFactory.getRStringD(10)).withFax(randomFactory.getRStringD(10))
                        .withEmail(randomFactory.getREmail(dn)).withEmail2(randomFactory.getREmail(dn))
                        .withEmail3(randomFactory.getREmail(dn)).withHomepage(randomFactory.getRWeb(dn))
                        .withBirthday(randomFactory.getRDate()).withAnniversary(randomFactory.getRDate())
                        .withPhone2(randomFactory.getRStringD(10)).withAddress2(randomFactory.getRString(rsl))
                        .withNotes(randomFactory.getRString(rsl));
                return (T) objectData;
            }
            else if(dataObject instanceof GroupData){
                GroupData objectData = (GroupData) dataObject;
                objectData.withName(randomFactory.getRString(rsl)).withHeader(randomFactory.getRString(rsl))
                        .withFooter(randomFactory.getRString(rsl));
                return (T) objectData;
            }
            else if(dataObject instanceof String){
                String objectData = (String) dataObject;
                if(dataType.equals("contacts")){
                    objectData = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                            randomFactory.getRString(rsl), randomFactory.getRString(rsl), randomFactory.getRString(rsl),
                            randomFactory.getRString(rsl), selectImage(), randomFactory.getRString(rsl),
                            randomFactory.getRString(rsl), randomFactory.getRString(rsl), randomFactory.getRStringD(10),
                            randomFactory.getRStringD(10), randomFactory.getRStringD(10),
                            randomFactory.getRStringD(10), randomFactory.getREmail(dn), randomFactory.getREmail(dn),
                            randomFactory.getREmail(dn), randomFactory.getRWeb(dn), randomFactory.getRDate(),
                            randomFactory.getRDate(), randomFactory.getRStringD(10), randomFactory.getRString(rsl),
                            randomFactory.getRString(rsl));
                }
                else{
                    objectData = String.format("%s,%s,%s",randomFactory.getRString(rsl), randomFactory.getRString(rsl),
                            randomFactory.getRString(rsl));
                }
                return (T) objectData;
            }
            else
                return null;
        }

        public void generateDataList() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            for(int ind=0;ind<recordsNum;ind++){
                if(dataMode.equals("r")) {
                    dataList.add(buildDataObject());
                }
                else if(dataMode.equals("i")){

                }
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        DataGenerator dataGenerator = new DataGenerator();
        JCommander jCommander = JCommander.newBuilder().addObject(dataGenerator).build();

        try{
            jCommander.parse(args);
        }
        catch (ParameterException pE){
            System.err.println("Invalid arguments format");
        }

        String target = System.getProperty("target");
        String fullPath = String.format(dataGenerator.filePath+"%s.properties",target);
        File gprop = new File(fullPath);
        dataGenerator.properties.load(new FileReader(gprop));
        dataGenerator.imagesPath = dataGenerator.properties.getProperty("imagesPath");
        dataGenerator.imagesType = dataGenerator.properties.getProperty("imagesType");
        File imagesFolder = new File(dataGenerator.imagesPath);

        Class genDataType;
        if(dataGenerator.fileType.equals("csv")){
            genDataType = String.class;
        }
        else{
            if(dataGenerator.dataType.equals("contacts"))
                genDataType = ContactData.class;
            else if(dataGenerator.dataType.equals("groups"))
                genDataType = GroupData.class;
            else{
                System.err.println("Unsupported object for generation");
                return;
            }
        }

        if(dataGenerator.imagesType.equals("")){
            dataGenerator.imagesList = imagesFolder.list();
        }
        else {
            dataGenerator.imagesList = imagesFolder.list(new FilenameFilter() {
                private Pattern pattern = Pattern.compile(dataGenerator.imagesType);

                @Override
                public boolean accept(File dir, String name) {
                    return pattern.matcher(name).matches();
                }
            });
        }
        dataGenerator.dn = Integer.parseInt(dataGenerator.properties.getProperty("domainsNum"));
        dataGenerator.rsl = Integer.parseInt(dataGenerator.properties.getProperty("randomStringLength"));

        DataGenerator.Generator generator = dataGenerator.new Generator(genDataType);
        generator.generateDataList();

        dataGenerator.writeToFile(generator.dataList, genDataType);
    }

    private <T> void writeToFile(List<T> dataList, Class genDataType) throws IOException {

        BufferedWriter formattedData = new BufferedWriter(new FileWriter(filePath+fileName+"."+fileType));

        if(fileType.equals("csv")){
            for(int j=0;j<dataList.size();j++){
                formattedData.write((String) dataList.get(j));
                formattedData.newLine();
            }
        }
        else if(fileType.equals("xml")){
            XStream xStream = new XStream();
            xStream.processAnnotations(genDataType);
            String xmlData = xStream.toXML(dataList);
            formattedData.write(xmlData);
        }
        else if(fileType.equals("json")){
            Gson gsonData = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
            String jsonData = gsonData.toJson(dataList);
            formattedData.write(jsonData);
        }
        else
            System.err.println("Undefined file type");

        formattedData.close();
    }
}
