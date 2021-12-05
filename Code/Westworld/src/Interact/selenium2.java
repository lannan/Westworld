package Interact;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class selenium2 {
    private List<String> preference;
    private List<String> simulator_settings;
    private String codePath;
    private String defaultValue;
    private String log = "";
    private String default_number = "6666666666";
    private String accounts_path = "accounts";
    private String default_time = "15:15";
    private List<String> defaults = Arrays.asList("on", "60%");
    private List<Integer> exploredSimInput;

    public selenium2(ArrayList<String> preferencesInputs, ArrayList<String> simulatorsInputs, String codePath, String defaultValue) {
        this.preference = preferencesInputs;
        this.simulator_settings = simulatorsInputs;
        this.codePath = codePath;
        this.defaultValue = defaultValue;
        this.exploredSimInput = new ArrayList<>();
    }

    public String getLog(){
        return this.log;
    }

    public void test() {
        long t1 = System.currentTimeMillis();
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        try {
            // read from input write to clipboard
            Map<String, String> map = get_email_account(accounts_path);
            int rand_int = new Random().nextInt(2);
            Iterator<String> iter = map.keySet().iterator();
            String email_address = iter.next();
            String password = map.get(email_address);
            for(int i = 0; i < rand_int; i++){
                email_address = iter.next();
                password = map.get(email_address);
            }
            String text = new String(Files.readAllBytes(Paths.get(codePath)), StandardCharsets.UTF_8);
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Clipboard clipboard = toolkit.getSystemClipboard();
            StringSelection strSel = new StringSelection(text);
            clipboard.setContents(strSel, null);
            System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
            driver.manage().window().maximize();
            //auto_Login(driver);
            driver.get("https://graph.api.smartthings.com/ide/apps");
            WebElement element = driver.findElement(By.xpath("/html/body/div[1]/main/div/div[2]/div/div/div[1]/form/button"));
            element.click();
            (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver wd) {
                    List<WebElement> ls = wd.findElements(By.xpath("/html/body/div[1]/main/div/div/h2"));
                    return ls != null && !ls.isEmpty();
                }
            });
            WebElement account = driver.findElement(By.xpath("//*[@id=\"iptLgnPlnID\"]"));
            slowly_sendKeys(email_address, account);
            WebElement passd = driver.findElement(By.xpath("//*[@id=\"iptLgnPlnPD\"]"));
            slowly_sendKeys(password, passd);
            WebElement pre_sign = driver.findElement(By.xpath("/html/body/div[1]/main/div/div/div[1]/form/fieldset/div[7]"));
            WebElement sign = pre_sign.findElement(By.id("signInButton"));
            Thread.sleep(1000);
            sign.click();
            (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/main/div[1]/div[1]/a[1]")));
            Thread.sleep(100);
            clearApps(driver);
            WebElement button_create = ((ChromeDriver) driver).findElementByXPath("/html/body/main/div[1]/div[1]/a[1]");
            button_create.click();
            (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/main/div/ul/li[2]/a")));
            Thread.sleep(100);
            WebElement from_code  = ((ChromeDriver) driver).findElementByXPath("/html/body/main/div/ul/li[2]/a");
            from_code.click();
            (new WebDriverWait(driver,20)).until(ExpectedConditions.visibilityOfElementLocated(By.id("content")));
            Thread.sleep(100);
            WebElement textArea = ((ChromeDriver) driver).findElementById("content");
            textArea.sendKeys(Keys.CONTROL + "v");
            Thread.sleep(1000);
            textArea.submit();
            (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Simulator')]")));
            Thread.sleep(100);
            WebElement simulator = driver.findElement(By.className("editor-menu")).findElement(By.xpath(".//*[contains(text(), 'Simulator')]"));
            simulator.click();
            simulate(driver);
            driver.quit();
            long t0 = System.currentTimeMillis();
            System.out.println((t0 - t1) + "ms");
        } catch (Exception e) {
            System.err.println("an error occurred with selenium");
            e.printStackTrace();
            driver.quit();
        }
    }
    private Map<String, String> get_email_account(String path) throws Exception {
        String text = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        String[] accounts = text.split("\n");
        if(accounts.length<2)
            throw new IllegalStateException();
        int len = accounts.length;
        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < accounts.length - 1; i++){
            map.put(accounts[i], accounts[len - 1]);
        }
        System.out.println(map);
        return map;
    }
    private void clearApps(WebDriver driver) throws Exception{
        WebElement sheet = null;
        try {
            sheet = driver.findElement(By.id("smartapp-table"));
        }catch (Exception e){}
        remove_locations(sheet);
        while(hasEdit(sheet)){
            WebElement edit = get_first_edit(sheet);
            edit.click();
            WebElement bar = null;
            try {
                (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(By.className("form-actions")));
                Thread.sleep(100);
                bar = driver.findElement(By.className("form-actions"));
            }catch (Exception e){ }
            WebElement delete = find_delete(bar);
            if(delete!=null)
                delete.click();
            else
                throw new IllegalStateException();

            sheet = null;
            try {
                (new WebDriverWait(driver,5)).until(ExpectedConditions.visibilityOfElementLocated(By.id("smartapp-table")));
                sheet = driver.findElement(By.id("smartapp-table"));
            }catch (Exception e){}
        }
    }
    private WebElement find_delete(WebElement element){
        if(element == null) return null;
        if(isAttributePresent(element,"value")){
            String debug = element.getAttribute("value");
            if(element.getAttribute("value").equals("Delete")){
            return element;
        }}
        List<WebElement> elements =  element.findElements(By.xpath("*"));
        for(WebElement element1:elements){
            WebElement el = find_delete(element1);
            if(el!=null){
                return el;
            }
        }
        return null;
    }
    private boolean hasEdit(WebElement element){
        if(element == null)
            return false;
        if(isEdit(element))
            return true;
        List<WebElement> childs = element.findElements(By.xpath("*"));
        for(WebElement element1:childs){
            if(hasEdit(element1))
                return true;
        }
        return false;
    }

    private WebElement get_first_edit(WebElement element){
        if(element == null)
            return null;
        if(isEdit(element))
            return element;
        List<WebElement> childs = element.findElements(By.xpath("*"));
        for(WebElement element1: childs){
            WebElement el = get_first_edit(element1);
            if(el!=null){
                return el;
            }
        }
        return null;
    }

    private boolean isEdit(WebElement element){
        if(element!=null&&isAttributePresent(element,"title")&&element.getAttribute("title").equals("Edit Properties")&&element.getTagName().equals("a")){
            return true;
        }
        return false;
    }

    private void remove_locations(WebElement element) throws Exception{
        if(element == null)
            return;
        if((element.getText().equals("yyy") || element.getText().equals("yyy2"))&&element.getAttribute("class").equals("label label-info")){
            System.out.println(element.getText());
            WebElement remove = element.findElement(By.xpath("*"));
            remove.click();
            Thread.sleep(100);
            return;
        }
        System.out.println("nothing...");
        List<WebElement> childs = element.findElements(By.xpath("*"));
        for(WebElement element1: childs){
            remove_locations(element1);
        }
    }

    private void simulate(WebDriver driver) throws Exception {
        WebDriverWait wait_set_location = new WebDriverWait(driver, 10);
        wait_set_location.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[5]/div/div/div[3]/div[2]/div[1]/div[2]/div/div[2]/button")));
        WebElement set_button = driver.findElement(By.xpath("/html/body/div[5]/div/div/div[3]/div[2]/div[1]/div[2]/div/div[2]/button"));
        set_button.click();

        WebDriverWait wait_preference = new WebDriverWait(driver, 10);
        wait_preference.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[5]/div/div/div[3]/div[2]/div[2]/div[2]/div")));
        Thread.sleep(1000);
        //begin to set preference
        WebElement panel = driver.findElement(By.xpath("/html/body/div[5]/div/div/div[3]/div[2]/div[2]/div[2]/div"));
        List<WebElement> sections = panel.findElements(By.className("section"));
        for (WebElement ele : sections) {
            set_preference(ele, preference);
        }

        WebElement install = driver.findElement(By.xpath("//*[@id=\"update\"]"));

        install.click();
        // wait for simulator come out
        WebDriverWait wait_log = new WebDriverWait(driver, 20);
        wait_log.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[5]/div/div/div[3]/div[2]/div[3]/div[2]/div[1]/div[2]")));
        Thread.sleep(1000);

        set_simulators_get_log(driver);

    }

    private void set_preference(WebElement element, List<String> preference) throws Exception {

        Map<String, WebElement> section_prefs = find_arrow_down(preference, new StringBuilder(), element);
        Map<String, WebElement> trays = getTray(new StringBuilder(), element);

        // click if options not displayted.
        for (String pref : section_prefs.keySet()) {
            String[] title_input = pref.split("@");
            String titles = title_input[0];

            if (!trays.containsKey(titles)) {
                section_prefs.get(pref).click();
                trays = getTray(new StringBuilder(), element);
            }
        }

        if (trays.size() != section_prefs.size()) throw new IllegalStateException();

        for (Map.Entry<String, WebElement> entry : trays.entrySet()) {
            boolean flag = false;
            String titles = entry.getKey();
            for (String pref : section_prefs.keySet()) {
                String[] titles_input = pref.split("@");
                if (titles_input[0].equals(titles)) {
                    flag = true;
                    WebElement tray = entry.getValue();
                    if (titles_input[1].equals("NOT_SPECIFIED")) {
                        if (!enter_input_tray(new StringBuilder(), tray, defaultValue))
                            not_input_by_default_or_specified(tray);
                    } else {
                        if (!enter_input_tray(new StringBuilder(), tray, titles_input[1]))
                            if (!enter_input_tray(new StringBuilder(), tray, defaultValue))
                                not_input_by_default_or_specified(tray);
                    }
                }
            }
            if (!flag) throw new IllegalStateException();
        }

    }

    private void set_simulators_get_log(WebDriver driver) throws Exception {
        List<Map<String, WebElement>> sim_maps = new ArrayList<>();
        Map<String, WebElement> selectMap = new HashMap<>();
        Map<String, WebElement> play_map = new HashMap<>();
        sim_maps.add(selectMap);
        sim_maps.add(play_map);

        Map<String, String> log_map = new HashMap<>();

        WebElement sim_panel = driver.findElement(By.id("ide-simulator-panel")).findElement(By.className("content"));
        find_select_play(sim_maps, new StringBuilder(), sim_panel);

        if(simulator_settings.size() == 0){
            set_simulator_no_sim_input(0, driver, selectMap, play_map, log_map);
        }else {

            for (String sim : selectMap.keySet()) {
                String setting = null;
                for (String sim_pref : simulator_settings) {
                    String[] sim_set = sim_pref.split("@");
                    String sim_name = sim_set[0].substring(1);
                    if (sim_name.equals(sim)) {
                        setting = sim_set[1];
                    }
                }

                WebElement selector = selectMap.get(sim);
                WebElement play = play_map.get(sim);

                if (play == null)
                    throw new IllegalStateException();

                if (setting == null) {
                    defautl_set_selector(selector, defaults);
                } else {
                    if (!set_selector(selector, setting)) {
                        defautl_set_selector(selector, defaults);
                    }
                }

                Thread.sleep(100);

                play.click();
                Thread.sleep(2000);
                WebElement console_log = driver.findElement(By.xpath("/html/body/div[5]/div/div/div[2]/div/div[2]/div[2]/div"));
                String log_info = console_log.getText();
                log_map.put(sim, log_info);

            }
        }
        int len = 0;
        for (String log : log_map.values()) {
            len += log.length();
        }
        if (len == 0) {
            WebElement uninstall = driver.findElement(By.xpath("//*[@id=\"stop\"]"));
            uninstall.click();
            (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver wd) {
                    WebElement element = null;
                    try {
                        element = wd.findElement(By.id("ide-simulator-panel")).findElement(By.className("content"));
                    } catch (Exception e) {
                    }
                    return element != null;
                }
            });
            simulate(driver);
            return;
        }

        for (String sim : log_map.keySet()) {
            //  String tmp = sim + ":" + "\n";
            String tmp = log_map.get(sim);
            //tmp += "\n";

            log += tmp;
        }
        System.out.println(log);

    }

    private void set_simulator_no_sim_input(int time, WebDriver driver, Map<String, WebElement> selectMap, Map<String, WebElement> playMap, Map<String, String> log_map) throws Exception{
        if(time == 3) return;
        exploredSimInput.clear();
        for(Map.Entry<String, WebElement> entry: selectMap.entrySet()){
            String key = entry.getKey();
            WebElement select = entry.getValue();
            if(!playMap.containsKey(key)) throw new IllegalStateException();
            WebElement play = playMap.get(key);
            exploredSimInput.add(randomSelect(select));
            play.click();
            Thread.sleep(2000);
            WebElement console_log = driver.findElement(By.xpath("/html/body/div[5]/div/div/div[2]/div/div[2]/div[2]/div"));
            String log_info = console_log.getText();
            if(log_info.length() != 0){
                log_map.put(key, log_info);
                return;
            }
        }

        set_simulator_no_sim_input(time + 1, driver, selectMap, playMap, log_map);

    }

    private int randomSelect(WebElement select){
        Select mselect = new Select(select);
        List<WebElement> options = mselect.getOptions();
        int index = new Random().nextInt(options.size());
        mselect.selectByIndex(index);
        return index;
    }


    private boolean set_selector(WebElement selector, String pref) {
        Select select = new Select(selector);
        List<WebElement> options = select.getOptions();
        for (WebElement option : options) {
            if (option.getText().equals(pref)) {
                select.selectByVisibleText(pref);
                return true;
            }
        }
        return false;
    }

    private void defautl_set_selector(WebElement selector, List<String> defaults) {
        Select select = new Select(selector);
        boolean flag = false;
        for (String def : defaults) {
            for (WebElement option : select.getOptions()) {
                if (option.getText().contains(def)) {
                    select.selectByVisibleText(option.getText());
                    flag = true;
                }
            }
        }

        if (!flag) {
            select.selectByIndex(select.getOptions().size() - 1);
        }
    }

    private Map<String, WebElement> find_arrow_down(List<String> preference, StringBuilder str, WebElement element) throws InterruptedException {
        Map<String, WebElement> ans = new HashMap<>();
        // some times parsed class str is not complete;
        if (element.getAttribute("class").contains("arrow down")) {
            for (String pref : preference) {
                if (pref.startsWith(str.toString())) {
                    ans.put(pref, element);
                    return ans;
                }else if(isMaskedByTitle(pref, str.toString())){
                    String[] tmps = pref.split("@");
                    if(tmps.length > 1) {
                        String tmp = tmps[tmps.length - 1];
                        String masked_rule = str.toString() + '@' + tmp;
                        ans.put(masked_rule, element);
                    }else
                        throw new IllegalStateException();

                    return ans;
                }
            }

            str.append("@NOT_SPECIFIED");
            ans.put(str.toString(), element);
            return ans;
        }
        List<WebElement> childs = element.findElements(By.xpath("*"));
        String current_title;
        for (WebElement child : childs) {
            String class_name = child.getAttribute("class");
            if (class_name.equals("title")) {
                current_title = child.getText();
                // only two titles are allowed
                int index = str.indexOf("#");
                if (index >= 0 && index < str.length()) {
                    str.delete(index, str.length());
                }

                str = str.length() != 0 ? str.append("#" + current_title) : str.append(current_title);
            }
            Map<String, WebElement> res = find_arrow_down(preference, str, child);
            MapDifference diff = Maps.difference(ans, res);
            // one title one input(tray, input);
            if (diff.entriesInCommon().size() != 0)
                throw new IllegalStateException();
            ans.putAll(res);
        }

        return ans;
    }

    private boolean isMaskedByTitle(String pref, String str){
        String[] tiles_input = pref.split("@");
        if(tiles_input.length > 1) {
            String[] titles = tiles_input[0].split("#");
            String[] titles_explored = str.split("#");
            for(String title: titles){
                boolean flag = false;
                for(String title_explored:titles_explored){
                    if(title.equals(title_explored)){
                        flag = true;
                        break;
                    }
                }
                if(!flag) return false;
            }
            return true;
        }else
            throw new IllegalStateException();


    }


    private Map<String, WebElement> getTray(StringBuilder titles, WebElement element) throws IllegalStateException {
        Map<String, WebElement> ans = new HashMap<>();
        if (element.getAttribute("class").equals("tray")) {
            ans.put(titles.toString(), element);
            return ans;
        }

        List<WebElement> childs = element.findElements(By.xpath("*"));
        String current_title = "";
        for (WebElement child : childs) {
            if (child.getAttribute("class").equals("title")) {
                current_title = child.getText();
                //only two titles are allowed;
                int index = titles.indexOf("#");
                if (index >= 0 && index < titles.length()) {
                    titles.delete(index, titles.length());
                }
                titles = titles.length() != 0 ? titles.append("#" + current_title) : titles.append(current_title);
            }
            Map<String, WebElement> res = getTray(titles, child);
            MapDifference diff = Maps.difference(ans, res);
            // one title one input(tray, input);
            if (diff.entriesInCommon().size() != 0)
                throw new IllegalStateException();
            ans.putAll(res);
        }

        return ans;
    }

    private boolean enter_input_tray(StringBuilder option_string, WebElement element, String input) {
        if(input.equals("empty"))
            return true;
        if(element.getTagName().equals("input")) {
            if (element.getAttribute("type").equals("radio") && option_string.length() > 0
                    && input.contains(option_string.toString())) {
                //option_string.delete(0, option_string.length());
                if (!element.isSelected()) element.click();
                return true;
            }
            else            if(element.getAttribute("type").equals("checkbox") && option_string.length() > 0
                    && input.contains(option_string.toString())) {
                //option_string.delete(0, option_string.length());
                if (!element.isSelected()) element.click();
                return true;
            }
            else if (element.getAttribute("type").equals("text")) {
                element.clear();
                if(isAttributePresent(element, "placeholder") && input.equals(defaultValue)
                        && element.getAttribute("placeholder").equals("###-###-####")) {
                    element.sendKeys(default_number);
                }
                else if(isAttributePresent(element, "placeholder") && input.equals(defaultValue)
                        && element.getAttribute("placeholder").equals("hh:mm")){
                    element.sendKeys(default_time);
                }
                else {
                    element.sendKeys(input);
                }
                return true;
            }
        } else if (element.getTagName().equals("select")) {
            Select sel = new Select(element);
            List<WebElement> options = sel.getOptions();
            for (WebElement option : options) {
                if (option.getText().contains(input)) {
                    String op = option.getText();
                    sel.selectByVisibleText(op);
                    return true;
                }
            }
        }
        List<WebElement> childs = element.findElements(By.xpath("*"));
        boolean flag = false;
        for (WebElement child : childs) {
            if(child.getTagName().equals("li")){
                option_string.delete(0, option_string.length());
                option_string.append(child.getText());
            }

            if (enter_input_tray(option_string, child, input)) {
                flag = true;
            }

        }
        return flag;

    }

    private boolean not_input_by_default_or_specified(WebElement element) {
        if (element.getTagName().equals("input")) {
            if (element.getAttribute("type").equals("radio")) {
                element.click();
                return true; // only click once;
            }
            if(element.getAttribute("type").equals("checkbox")){
                if (!element.isSelected()) element.click();
                //return true; // all checkbox should be checked
            }
        } else if (element.getTagName().equals("select")) {
            Select sel = new Select(element);
            sel.selectByIndex(1);
        }
        List<WebElement> childs = element.findElements(By.xpath("*"));
        for (WebElement child : childs) {
            if(not_input_by_default_or_specified(child))
                return true;
        }
        return false;
    }

    private void find_select_play(List<Map<String, WebElement>> maps, StringBuilder sim_name, WebElement panel) {
        String debug = panel.getAttribute("class");
        if (panel.getAttribute("class").equals("deviceLabel")) {
            sim_name.delete(0, sim_name.length());
            sim_name.append(panel.getText());
        }
        if (panel.getTagName().equals("select")) {
            maps.get(0).put(sim_name.toString(), panel);
        } else if (panel.getTagName().equals("button")) {
            maps.get(1).put(sim_name.toString(), panel);
        }

        List<WebElement> childs = panel.findElements(By.xpath("*"));
        for (WebElement child : childs) {
            find_select_play(maps, sim_name, child);
        }
    }

    private void slowly_sendKeys(String key, WebElement input) throws InterruptedException {
        for (int i = 0; i < key.length(); i++) {
            Thread.sleep(20);
            String nextChar = String.valueOf(key.charAt(i));
            input.sendKeys(nextChar);
        }
    }

    private boolean isAttributePresent(WebElement element, String attribute){
        boolean isPresent = false;
        try{
            String val = element.getAttribute(attribute);
            if(val == null)
                isPresent = false;
            else
                isPresent = true;
        }catch (Exception e){
        }
        return isPresent;
    }

}


//    private void auto_Login(WebDriver driver) throws FileNotFoundException {
//        FileReader reader = new FileReader("cookies.json");
//        JsonParser parser = new JsonParser();
//        JsonArray array = (JsonArray)parser.parse(reader);
//
//        for(Object o: array){
//            String str = ((JsonObject)o).toString();
//            Map<String, String> map = createMapFromJsonStr(str);
//
//            String date_str = map.get("expirationDate");
//            Date tmp =  null;
//            try{
//                tmp = DateFormat.getDateInstance(DateFormat.FULL).parse(date_str);
//            }catch (Exception e){}
//            Date expiryDate = tmp;
//
//            Boolean isSecure = Boolean.parseBoolean(map.get("expirationDate"));
//            Boolean isHttpOnly = Boolean.parseBoolean(map.get("httpOnly"));
//
//            Cookie cookie = new Cookie(map.get("name"), map.get("value"), map.get("domain")
//                    , map.get("path"), expiryDate, isSecure, isHttpOnly);
//            try {
//                driver.manage().addCookie(cookie);
//            }catch (Exception e){ }
//        }
//    }
//
//    private Map<String, String> createMapFromJsonStr(String str){
//        Map<String, String> ans = new HashMap<>();
//        int j = 0;
//        String key = "";
//        String value = "";
//        for(int i = 0; i < str.length(); i++){
//            if(str.charAt(i) == ':'){
//                while(str.charAt(j) == '{' || str.charAt(j) == '"') j++;
//                int k = i;
//                while(str.charAt(k) == '{' || str.charAt(k) == '"' || str.charAt(k) == ':') k--;
//                key = str.substring(j, k + 1);
//                j = i + 1;
//            }else if(str.charAt(i) == ','){
//                while(str.charAt(j) == '{' || str.charAt(j) == '"') j++;
//                int k = i;
//                while(str.charAt(k) == '{' || str.charAt(k) == '"' || str.charAt(k) == ',') k--;
//                value = str.substring(j, k + 1);
//                j = i + 1;
//
//                ans.put(key, value);
//            }
//
//        }
//        return ans;
//    }

//        WebElement selector = ((ChromeDriver) driver).findElementByXPath("/html/body/div[5]/div/div/div[3]/div[2]/div[3]/div[2]/div[1]/div[2]/div[4]/select");
//        Select select = new Select(selector);
//
//        WebElement console_log = driver.findElement(By.xpath("/html/body/div[5]/div/div/div[2]/div/div[2]/div[2]/div"));
//
//        List<WebElement> options = select.getOptions();
//        for (int i = 0; i < options.size(); i++) {
//            select.selectByIndex(i);
//            Thread.sleep(500);
//            WebElement start = driver.findElement(By.xpath("/html/body/div[5]/div/div/div[3]/div[2]/div[3]/div[2]/div[1]/div[2]/div[4]/button"));
//            start.click();
//            long t2 = System.currentTimeMillis();
//            String tmp = console_log.getText();
//            while (tmp.equals(log)) {
//                Thread.sleep(200);
//                long t3 = System.currentTimeMillis();
//                if (t3 - t2 > 3000)
//                    break;
//                tmp = console_log.getText();
//            }
//            if (log.equals(tmp)) {
//                System.out.println("********************************");
//                continue;
//            }
//            log = tmp;
//            System.out.println(log);
//        }

//    private void selectText() throws Exception {
//        int init_x = 45;
//        int init_y = 230;
//        Robot robot = new Robot();
//        robot.mouseMove(init_x, init_y);
//        robot.delay(80);
//        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//        for (int i = 0; i < 5; i++) {
//            robot.delay(80);
//            init_x += 90;
//            init_y += 37;
//            robot.mouseMove(init_x, init_y);
//        }
//        //robot.delay(8000);
//        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//        robot.keyPress(17);
//        Thread.sleep(1);
//        robot.keyPress(65);
//        Thread.sleep(100);
//        robot.keyRelease(65);
//        robot.keyRelease(17);
//    }
//
//    private void delete() throws Exception {
//        Robot robot = new Robot();
//        robot.keyPress(8);
//    }
//
//    private void paste() throws Exception {
//        Robot robot = new Robot();
//        robot.keyPress(17);
//        Thread.sleep(1);
//        robot.keyPress(86);
//        Thread.sleep(100);
//        robot.keyRelease(86);
//        robot.keyRelease(17);
//    }