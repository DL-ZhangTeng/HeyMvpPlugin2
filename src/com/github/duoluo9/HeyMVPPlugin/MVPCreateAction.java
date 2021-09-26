package com.github.duoluo9.HeyMVPPlugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MVPCreateAction extends AnAction {
    private Project project;
    private Module module;
    private String packageName = "";
    private String packageNameFull = "";
    private String mAuthor = "Hey";
    private String mModuleName;
    private String type;
    private String name;
    private String appPath;
    private MVPCreateAction.CodeType mType;
    private String layoutName = "";
    private boolean isKt = false;

    public void actionPerformed(AnActionEvent e) {
        project = e.getData(PlatformDataKeys.PROJECT);
        module = (Module) e.getDataContext().getData("module");
        packageName = getPackageName();
        Map<String, String> map = System.getenv();
        mAuthor = map.get("USERNAME");// 获取用户名
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (virtualFile != null) {
            appPath = virtualFile.getPath();//项目根路径+包名+被选择的文件夹路径
        } else {
            appPath = getAppPath();//项目根路径+包名
        }
        String[] strings = appPath.split("src/main/java/");
        packageNameFull = strings.length > 1 ? strings[1].replace("/", ".") : packageName;
        this.showDialog();
        this.refreshProject();
    }

    private void showDialog() {
        CreateDialog myDialog = new CreateDialog((name) -> {
            if (!name.contains("Fragment") && !name.contains("fragment")) {
                this.mModuleName = name.split("Activity")[0];
                this.mType = MVPCreateAction.CodeType.Activity;
                this.name = "Activity";
            } else {
                this.mModuleName = name.split("Fragment")[0];
                this.mType = MVPCreateAction.CodeType.Fragment;
                this.name = "Fragment";
            }

            this.mModuleName.replace("Fragment", "");
            this.mModuleName.replace("fragment", "");
            this.mModuleName.replace("Activity", "");
            this.mModuleName.replace("activity", "");

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(module.getName().substring(module.getName().indexOf(".") + 1).toLowerCase());
            stringBuilder.append("_");
            stringBuilder.append(this.name.toLowerCase());
            ArrayList<String> activityChildNames = splitByUpperCase(mModuleName);
            for (String activityChildName : activityChildNames) {
                stringBuilder.append("_").append(activityChildName.toLowerCase());
            }

            this.layoutName = stringBuilder.toString();
            this.createClassFiles();
            Messages.showInfoMessage(this.project, "create mvp code success", "title");
        });
        myDialog.setVisible(true);
    }

    private void refreshProject() {
        try {
            project.getBaseDir().refresh(false, true);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void createClassFiles() {
        this.createClassFile(this.mType);
        this.createClassFile(MVPCreateAction.CodeType.Presenter);
        this.createClassFile(MVPCreateAction.CodeType.Layout);
    }

    private void createClassFile(MVPCreateAction.CodeType codeType) {
        String fileName;
        String content;
        switch (codeType) {
            case Activity:
                this.type = "Activity";
                this.name = "Activity";
                fileName = isKt ? "TemplateActivity.kt.ftl" : "TemplateActivity.java.ftl";
                content = this.ReadTemplateFile(fileName);
                content = this.dealTemplateContent(content);
                if (this.isKt) {
                    this.writeToFile(content, appPath, this.mModuleName + "Activity.kt");
                } else {
                    this.writeToFile(content, appPath, this.mModuleName + "Activity.java");
                }

                this.editManifest(this.mModuleName + "Activity");
                break;
            case Fragment:
                this.type = "Fragment";
                this.name = "Fragment";
                fileName = isKt ? "TemplateFragment.kt.ftl" : "TemplateFragment.java.ftl";
                content = this.ReadTemplateFile(fileName);
                content = this.dealTemplateContent(content);
                if (this.isKt) {
                    this.writeToFile(content, appPath, this.mModuleName + "Fragment.kt");
                } else {
                    this.writeToFile(content, appPath, this.mModuleName + "Fragment.java");
                }
                break;
            case Presenter:
                this.type = "Presenter";
                fileName = isKt ? "TemplatePresenter.kt.ftl" : "TemplatePresenter.java.ftl";
                content = this.ReadTemplateFile(fileName);
                content = this.dealTemplateContent(content);
                if (this.isKt) {
                    this.writeToFile(content, appPath, this.mModuleName + "Presenter.kt");
                } else {
                    this.writeToFile(content, appPath, this.mModuleName + "Presenter.java");
                }
                break;
            case Layout:
                this.type = "Layout";
                fileName = isKt ? "TemplateLayout.kt.ftl" : "TemplateLayout.java.ftl";
                content = this.ReadTemplateFile(fileName);
                content = this.dealTemplateContent(content);
                this.writeToFile(content, getLayoutPath(), this.layoutName + ".xml");
        }

    }

    /**
     * 获取包名文件路径
     *
     * @return
     */
    private String getAppPath() {
        String packagePath = packageName.replace(".", "/");
        return project.getBasePath() + "/" + module.getName().substring(module.getName().indexOf(".") + 1) + "/src/main/java/" + packagePath + "/";
    }

    /**
     * 获取包名文件路径
     *
     * @return
     */
    private String getLayoutPath() {
        return project.getBasePath() + "/" + module.getName().substring(module.getName().indexOf(".") + 1) + "/src/main/res/layout/";
    }

    private String dealTemplateContent(String content) {
        content = content.replace("$name", this.mModuleName);
        if (content.contains("$packagename")) {
            content = content.replace("$packagename", packageNameFull);
        }

        if (content.contains("$basepackagename")) {
            if (this.isKt) {
                content = content.replace("$basepackagename", "com.sskj.base." + this.type.toLowerCase());
            } else {
                content = content.replace("$basepackagename", "com.sskj.base");
            }
        }

        if (content.contains("$package")) {
            content = content.replace("$package", this.packageNameFull);
        }

        content = content.replace("$date", this.getDate());
        content = content.replace("$end", this.name);
        content = content.replace("$layout", this.layoutName);
        content = content.replace("$author", this.mAuthor);
        return content.replace(" $rPackageName", this.packageName);
    }

    public String getDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    private String ReadTemplateFile(String fileName) {
        InputStream in = null;
        if (this.isKt) {
            in = this.getClass().getResourceAsStream("/com/github/duoluo9/HeyMVPPlugin/TemplateKt/" + fileName);
        } else {
            in = this.getClass().getResourceAsStream("/com/github/duoluo9/HeyMVPPlugin/Template/" + fileName);
        }

        String content = "";

        try {
            content = new String(this.readStream(in));
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return content;
    }

    private byte[] readStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        if (inputStream == null) return buffer;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = -1;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
            inputStream.close();
        }

        return outputStream.toByteArray();
    }

    /**
     * 生成
     *
     * @param content   类中的内容
     * @param classPath 类文件路径
     * @param className 类文件名称
     */
    private void writeToFile(String content, String classPath, String className) {
        try {
            File floder = new File(classPath);
            if (!floder.exists()) {
                floder.mkdirs();
            }

            File file = new File(classPath + "/" + className);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 从AndroidManifest.xml文件中获取当前app的包名
     *
     * @return
     */
    private String getPackageName() {
        String package_name = "";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/" + module.getName().substring(module.getName().indexOf(".") + 1) + "/src/main/AndroidManifest.xml");

            NodeList nodeList = doc.getElementsByTagName("manifest");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;
                package_name = element.getAttribute("package");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return package_name;
    }

    private void editManifest(String name) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/" + module.getName().substring(module.getName().indexOf(".") + 1) + "/src/main/AndroidManifest.xml");
            NodeList nodeList1 = doc.getElementsByTagName("application");

            for (int j = 0; j < nodeList1.getLength(); ++j) {
                Node node = nodeList1.item(j);
                Element application = (Element) node;
                Element a = doc.createElement("activity");
                a.setAttribute("android:name", packageNameFull + "." + name);
                application.appendChild(a);
            }

            saveXml(project.getBasePath() + "/" + module.getName().substring(module.getName().indexOf(".") + 1) + "/src/main/AndroidManifest.xml", doc);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void saveXml(String fileName, Document doc) {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            DOMSource source = new DOMSource();
            source.setNode(doc);
            StreamResult result = new StreamResult();
            result.setOutputStream(new FileOutputStream(fileName));
            transformer.transform(source, result);
        } catch (TransformerException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据大写字母拆分数组
     */
    private ArrayList<String> splitByUpperCase(String str) {
        ArrayList<String> rs = new ArrayList<String>();
        int index = 0;
        int len = str.length();
        for (int i = 1; i < len; i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                rs.add(str.substring(index, i));
                index = i;
            }
        }
        rs.add(str.substring(index, len));
        return rs;
    }

    private static enum CodeType {
        Activity,
        Fragment,
        Presenter,
        BaseView,
        BasePresenter,
        MvpBaseActivity,
        MvpBaseFragment,
        Layout;
    }
}

