package com.cisco.dft.sdk.vcs.util;

import java.util.HashMap;
import java.util.Map;

import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;

public abstract class FileExtensionMapping {
	
	private FileExtensionMapping(){}

	static final Map<String, Language> FILE_EXTENSION_ASSOCIATIONS = new HashMap<String, Language>(); 
			
	static {
		FILE_EXTENSION_ASSOCIATIONS.put("java", Language.JAVA);
		FILE_EXTENSION_ASSOCIATIONS.put("cs", Language.CSHARP);
		FILE_EXTENSION_ASSOCIATIONS.put("c", Language.C);
		FILE_EXTENSION_ASSOCIATIONS.put("cc", Language.CPP);
		FILE_EXTENSION_ASSOCIATIONS.put("cpp", Language.CPP);
		FILE_EXTENSION_ASSOCIATIONS.put("cxx", Language.CPP);
		FILE_EXTENSION_ASSOCIATIONS.put("h", Language.C);
		FILE_EXTENSION_ASSOCIATIONS.put("hpp", Language.CPP);
		FILE_EXTENSION_ASSOCIATIONS.put("hxx", Language.CPP);
		FILE_EXTENSION_ASSOCIATIONS.put("js", Language.JAVASCRIPT);
		FILE_EXTENSION_ASSOCIATIONS.put("jsp", Language.JSP);
		FILE_EXTENSION_ASSOCIATIONS.put("py", Language.PYTHON);
		FILE_EXTENSION_ASSOCIATIONS.put("pyw", Language.PYTHON);
		FILE_EXTENSION_ASSOCIATIONS.put("lua", Language.LUA);
		FILE_EXTENSION_ASSOCIATIONS.put("html", Language.HTML);
		FILE_EXTENSION_ASSOCIATIONS.put("xhtml", Language.HTML);
		FILE_EXTENSION_ASSOCIATIONS.put("shtml", Language.HTML);
		FILE_EXTENSION_ASSOCIATIONS.put("php", Language.PHP);
		FILE_EXTENSION_ASSOCIATIONS.put("php3", Language.PHP);
		FILE_EXTENSION_ASSOCIATIONS.put("phpt", Language.PHP);
		FILE_EXTENSION_ASSOCIATIONS.put("phtml", Language.PHP);
		FILE_EXTENSION_ASSOCIATIONS.put("xml", Language.XML);
		FILE_EXTENSION_ASSOCIATIONS.put("css", Language.CSS);
		FILE_EXTENSION_ASSOCIATIONS.put("yml", Language.YAML);
		FILE_EXTENSION_ASSOCIATIONS.put("abap", Language.ABAP);
		FILE_EXTENSION_ASSOCIATIONS.put("as", Language.ACTIONSCRIPT);
		FILE_EXTENSION_ASSOCIATIONS.put("ada", Language.ADA); 
		FILE_EXTENSION_ASSOCIATIONS.put("adb", Language.ADA); 
		FILE_EXTENSION_ASSOCIATIONS.put("ads", Language.ADA); 
		FILE_EXTENSION_ASSOCIATIONS.put("pad", Language.ADA);
		FILE_EXTENSION_ASSOCIATIONS.put("adso", Language.ADSO_IDSM);
		FILE_EXTENSION_ASSOCIATIONS.put("ample", Language.AMPLE); 
		FILE_EXTENSION_ASSOCIATIONS.put("dofile", Language.AMPLE); 
		FILE_EXTENSION_ASSOCIATIONS.put("startup", Language.AMPLE);
		FILE_EXTENSION_ASSOCIATIONS.put("build.xml", Language.ANT);
		FILE_EXTENSION_ASSOCIATIONS.put("trigger", Language.APEX_TRIGGER);
		FILE_EXTENSION_ASSOCIATIONS.put("ino", Language.ARDUINO_SKETCH); 
		FILE_EXTENSION_ASSOCIATIONS.put("pde", Language.ARDUINO_SKETCH);
		FILE_EXTENSION_ASSOCIATIONS.put("asa", Language.ASP); 
		FILE_EXTENSION_ASSOCIATIONS.put("asp", Language.ASP);
		FILE_EXTENSION_ASSOCIATIONS.put("asax", Language.ASPdotNET); 
		FILE_EXTENSION_ASSOCIATIONS.put("ascx", Language.ASPdotNET); 
		FILE_EXTENSION_ASSOCIATIONS.put("asmx", Language.ASPdotNET); 
		FILE_EXTENSION_ASSOCIATIONS.put("aspx", Language.ASPdotNET); 
		FILE_EXTENSION_ASSOCIATIONS.put("config", Language.ASPdotNET); 
		FILE_EXTENSION_ASSOCIATIONS.put("master", Language.ASPdotNET); 
		FILE_EXTENSION_ASSOCIATIONS.put("sitemap", Language.ASPdotNET);
		FILE_EXTENSION_ASSOCIATIONS.put("webinfo", Language.ASPdotNET);
		FILE_EXTENSION_ASSOCIATIONS.put("asm", Language.ASSEMBLY);
		FILE_EXTENSION_ASSOCIATIONS.put("s", Language.ASSEMBLY);
		FILE_EXTENSION_ASSOCIATIONS.put("S", Language.ASSEMBLY);
		FILE_EXTENSION_ASSOCIATIONS.put("ahk", Language.AUTOHOTKEY);
		FILE_EXTENSION_ASSOCIATIONS.put("awk", Language.AWK);
		FILE_EXTENSION_ASSOCIATIONS.put("bash", Language.BOURNE_AGAIN_SHELL);
		FILE_EXTENSION_ASSOCIATIONS.put("sh", Language.BOURNE_AGAIN);
		FILE_EXTENSION_ASSOCIATIONS.put("c", Language.C);
		FILE_EXTENSION_ASSOCIATIONS.put("ec", Language.C);
		FILE_EXTENSION_ASSOCIATIONS.put("pgc", Language.C);
		/*FILE_EXTENSION_ASSOCIATIONS.put("csh");
		FILE_EXTENSION_ASSOCIATIONS.put("tcsh");
		FILE_EXTENSION_ASSOCIATIONS.put("cs");
		FILE_EXTENSION_ASSOCIATIONS.put("C");
		FILE_EXTENSION_ASSOCIATIONS.put("c++", Language.C);
		FILE_EXTENSION_ASSOCIATIONS.put("cc");
		FILE_EXTENSION_ASSOCIATIONS.put("cpp");
		FILE_EXTENSION_ASSOCIATIONS.put("cxx");
		FILE_EXTENSION_ASSOCIATIONS.put("pcc");
		FILE_EXTENSION_ASSOCIATIONS.put("h");
		FILE_EXTENSION_ASSOCIATIONS.put("H");
		FILE_EXTENSION_ASSOCIATIONS.put("hh");
		FILE_EXTENSION_ASSOCIATIONS.put("hpp");
		FILE_EXTENSION_ASSOCIATIONS.put("ccs");
		FILE_EXTENSION_ASSOCIATIONS.put("clj");
		FILE_EXTENSION_ASSOCIATIONS.put("cljs");
		FILE_EXTENSION_ASSOCIATIONS.put("cmake");
		FILE_EXTENSION_ASSOCIATIONS.put("CMakeLists.txt");
		FILE_EXTENSION_ASSOCIATIONS.put("cbl");
		FILE_EXTENSION_ASSOCIATIONS.put("CBL");
		FILE_EXTENSION_ASSOCIATIONS.put("cob");
		FILE_EXTENSION_ASSOCIATIONS.put("COB");
		FILE_EXTENSION_ASSOCIATIONS.put("coffee");
		FILE_EXTENSION_ASSOCIATIONS.put("cfm");
		FILE_EXTENSION_ASSOCIATIONS.put("cfc");
		FILE_EXTENSION_ASSOCIATIONS.put("css");
		FILE_EXTENSION_ASSOCIATIONS.put("cu");
		FILE_EXTENSION_ASSOCIATIONS.put("pyx");
		FILE_EXTENSION_ASSOCIATIONS.put("d");
		FILE_EXTENSION_ASSOCIATIONS.put("da");
		FILE_EXTENSION_ASSOCIATIONS.put("dart");
		FILE_EXTENSION_ASSOCIATIONS.put("diff");
		FILE_EXTENSION_ASSOCIATIONS.put("dita");
		FILE_EXTENSION_ASSOCIATIONS.put("bat");
		FILE_EXTENSION_ASSOCIATIONS.put("BAT");
		FILE_EXTENSION_ASSOCIATIONS.put("btm");
		FILE_EXTENSION_ASSOCIATIONS.put("BTM");
		FILE_EXTENSION_ASSOCIATIONS.put("cmd");
		FILE_EXTENSION_ASSOCIATIONS.put("CMD");
		FILE_EXTENSION_ASSOCIATIONS.put("dtd");
		FILE_EXTENSION_ASSOCIATIONS.put("ecpp");
		FILE_EXTENSION_ASSOCIATIONS.put("ex");
		FILE_EXTENSION_ASSOCIATIONS.put("exs");
		FILE_EXTENSION_ASSOCIATIONS.put("ERB");
		FILE_EXTENSION_ASSOCIATIONS.put("erb");
		FILE_EXTENSION_ASSOCIATIONS.put("erl");
		FILE_EXTENSION_ASSOCIATIONS.put("hrl");
		FILE_EXTENSION_ASSOCIATIONS.put("exp");
		FILE_EXTENSION_ASSOCIATIONS.put("fs");
		FILE_EXTENSION_ASSOCIATIONS.put("fsi");
		FILE_EXTENSION_ASSOCIATIONS.put("focexec");
		FILE_EXTENSION_ASSOCIATIONS.put("f");
		FILE_EXTENSION_ASSOCIATIONS.put("F");
		FILE_EXTENSION_ASSOCIATIONS.put("f77");
		FILE_EXTENSION_ASSOCIATIONS.put("F77");
		FILE_EXTENSION_ASSOCIATIONS.put("for");
		FILE_EXTENSION_ASSOCIATIONS.put("FOR");
		FILE_EXTENSION_ASSOCIATIONS.put("FTN");
		FILE_EXTENSION_ASSOCIATIONS.put("ftn");
		FILE_EXTENSION_ASSOCIATIONS.put("pfo");
		FILE_EXTENSION_ASSOCIATIONS.put("f90");
		FILE_EXTENSION_ASSOCIATIONS.put("F90");
		FILE_EXTENSION_ASSOCIATIONS.put("f95");
		FILE_EXTENSION_ASSOCIATIONS.put("F95");
		FILE_EXTENSION_ASSOCIATIONS.put("go");
		FILE_EXTENSION_ASSOCIATIONS.put("gsp");
		FILE_EXTENSION_ASSOCIATIONS.put("gant");
		FILE_EXTENSION_ASSOCIATIONS.put("gradle");
		FILE_EXTENSION_ASSOCIATIONS.put("groovy");
		FILE_EXTENSION_ASSOCIATIONS.put("haml");
		FILE_EXTENSION_ASSOCIATIONS.put("handlebars");
		FILE_EXTENSION_ASSOCIATIONS.put("hbs");
		FILE_EXTENSION_ASSOCIATIONS.put("hb");
		FILE_EXTENSION_ASSOCIATIONS.put("hs");
		FILE_EXTENSION_ASSOCIATIONS.put("lhs");
		FILE_EXTENSION_ASSOCIATIONS.put("cg");
		FILE_EXTENSION_ASSOCIATIONS.put("cginc");
		FILE_EXTENSION_ASSOCIATIONS.put("shader");
		FILE_EXTENSION_ASSOCIATIONS.put("htm");
		FILE_EXTENSION_ASSOCIATIONS.put("html");
		FILE_EXTENSION_ASSOCIATIONS.put("idl");
		FILE_EXTENSION_ASSOCIATIONS.put("pro");
		FILE_EXTENSION_ASSOCIATIONS.put("ism");
		FILE_EXTENSION_ASSOCIATIONS.put("java");
		FILE_EXTENSION_ASSOCIATIONS.put("js");
		FILE_EXTENSION_ASSOCIATIONS.put("jsf");
		FILE_EXTENSION_ASSOCIATIONS.put("xhtml");
		FILE_EXTENSION_ASSOCIATIONS.put("jcl");
		FILE_EXTENSION_ASSOCIATIONS.put("json");
		FILE_EXTENSION_ASSOCIATIONS.put("jsp");
		FILE_EXTENSION_ASSOCIATIONS.put("jspf");
		FILE_EXTENSION_ASSOCIATIONS.put("ksc");
		FILE_EXTENSION_ASSOCIATIONS.put("ksh");
		FILE_EXTENSION_ASSOCIATIONS.put("kt");
		FILE_EXTENSION_ASSOCIATIONS.put("less");
		FILE_EXTENSION_ASSOCIATIONS.put("l");
		FILE_EXTENSION_ASSOCIATIONS.put("el");
		FILE_EXTENSION_ASSOCIATIONS.put("lisp");
		FILE_EXTENSION_ASSOCIATIONS.put("lsp");
		FILE_EXTENSION_ASSOCIATIONS.put("sc");
		FILE_EXTENSION_ASSOCIATIONS.put("jl");
		FILE_EXTENSION_ASSOCIATIONS.put("cl");
		FILE_EXTENSION_ASSOCIATIONS.put("oscript");
		FILE_EXTENSION_ASSOCIATIONS.put("lua");
		FILE_EXTENSION_ASSOCIATIONS.put("ac");
		FILE_EXTENSION_ASSOCIATIONS.put("m4");
		FILE_EXTENSION_ASSOCIATIONS.put("am");
		FILE_EXTENSION_ASSOCIATIONS.put("gnumakefile");
		FILE_EXTENSION_ASSOCIATIONS.put("Gnumakefile");
		FILE_EXTENSION_ASSOCIATIONS.put("makefile");
		FILE_EXTENSION_ASSOCIATIONS.put("Makefile");
		FILE_EXTENSION_ASSOCIATIONS.put("m");
		FILE_EXTENSION_ASSOCIATIONS.put("pom");
		FILE_EXTENSION_ASSOCIATIONS.put("pom.xml");
		FILE_EXTENSION_ASSOCIATIONS.put("i3");
		FILE_EXTENSION_ASSOCIATIONS.put("ig");
		FILE_EXTENSION_ASSOCIATIONS.put("m3");
		FILE_EXTENSION_ASSOCIATIONS.put("mg");
		FILE_EXTENSION_ASSOCIATIONS.put("csproj");
		FILE_EXTENSION_ASSOCIATIONS.put("vbproj");
		FILE_EXTENSION_ASSOCIATIONS.put("vcproj");
		FILE_EXTENSION_ASSOCIATIONS.put("wdproj");
		FILE_EXTENSION_ASSOCIATIONS.put("wixproj");
		FILE_EXTENSION_ASSOCIATIONS.put("mps");
		FILE_EXTENSION_ASSOCIATIONS.put("m");
		FILE_EXTENSION_ASSOCIATIONS.put("mustache");
		FILE_EXTENSION_ASSOCIATIONS.put("mxml");
		FILE_EXTENSION_ASSOCIATIONS.put("build");
		FILE_EXTENSION_ASSOCIATIONS.put("dmap");
		FILE_EXTENSION_ASSOCIATIONS.put("m");
		FILE_EXTENSION_ASSOCIATIONS.put("mm");
		FILE_EXTENSION_ASSOCIATIONS.put("ml");
		FILE_EXTENSION_ASSOCIATIONS.put("mli");
		FILE_EXTENSION_ASSOCIATIONS.put("mll");
		FILE_EXTENSION_ASSOCIATIONS.put("mly");
		FILE_EXTENSION_ASSOCIATIONS.put("fmt");
		FILE_EXTENSION_ASSOCIATIONS.put("rex");
		FILE_EXTENSION_ASSOCIATIONS.put("dpr");
		FILE_EXTENSION_ASSOCIATIONS.put("p");
		FILE_EXTENSION_ASSOCIATIONS.put("pas");
		FILE_EXTENSION_ASSOCIATIONS.put("pp");
		FILE_EXTENSION_ASSOCIATIONS.put("pcl");
		FILE_EXTENSION_ASSOCIATIONS.put("ses");
		FILE_EXTENSION_ASSOCIATIONS.put("perl");
		FILE_EXTENSION_ASSOCIATIONS.put("plh");
		FILE_EXTENSION_ASSOCIATIONS.put("plx");
		FILE_EXTENSION_ASSOCIATIONS.put("pm");
		FILE_EXTENSION_ASSOCIATIONS.put("PL");
		FILE_EXTENSION_ASSOCIATIONS.put("pl");
		FILE_EXTENSION_ASSOCIATIONS.put("php");
		FILE_EXTENSION_ASSOCIATIONS.put("php3");
		FILE_EXTENSION_ASSOCIATIONS.put("php4");
		FILE_EXTENSION_ASSOCIATIONS.put("php5");
		FILE_EXTENSION_ASSOCIATIONS.put("inc");
		FILE_EXTENSION_ASSOCIATIONS.put("pig");
		FILE_EXTENSION_ASSOCIATIONS.put("pl1");
		FILE_EXTENSION_ASSOCIATIONS.put("ps1");
		FILE_EXTENSION_ASSOCIATIONS.put("P");
		FILE_EXTENSION_ASSOCIATIONS.put("proto");
		FILE_EXTENSION_ASSOCIATIONS.put("purs");
		FILE_EXTENSION_ASSOCIATIONS.put("py");
		FILE_EXTENSION_ASSOCIATIONS.put("qml");
		FILE_EXTENSION_ASSOCIATIONS.put("R");
		FILE_EXTENSION_ASSOCIATIONS.put("rkt");
		FILE_EXTENSION_ASSOCIATIONS.put("rktl");
		FILE_EXTENSION_ASSOCIATIONS.put("sch");
		FILE_EXTENSION_ASSOCIATIONS.put("scm");
		FILE_EXTENSION_ASSOCIATIONS.put("scrbl");
		FILE_EXTENSION_ASSOCIATIONS.put("ss");
		FILE_EXTENSION_ASSOCIATIONS.put("cshtml");
		FILE_EXTENSION_ASSOCIATIONS.put("rexx");
		FILE_EXTENSION_ASSOCIATIONS.put("robot");
		FILE_EXTENSION_ASSOCIATIONS.put("tsv");
		FILE_EXTENSION_ASSOCIATIONS.put("rake");
		FILE_EXTENSION_ASSOCIATIONS.put("rb");
		FILE_EXTENSION_ASSOCIATIONS.put("rhtml");
		FILE_EXTENSION_ASSOCIATIONS.put("rs");
		FILE_EXTENSION_ASSOCIATIONS.put("sas");
		FILE_EXTENSION_ASSOCIATIONS.put("sass");
		FILE_EXTENSION_ASSOCIATIONS.put("scss");
		FILE_EXTENSION_ASSOCIATIONS.put("scala");
		FILE_EXTENSION_ASSOCIATIONS.put("sed");
		FILE_EXTENSION_ASSOCIATIONS.put("il");
		FILE_EXTENSION_ASSOCIATIONS.put("ils");
		FILE_EXTENSION_ASSOCIATIONS.put("smarty");
		FILE_EXTENSION_ASSOCIATIONS.put("tpl");
		FILE_EXTENSION_ASSOCIATIONS.put("sbl");
		FILE_EXTENSION_ASSOCIATIONS.put("SBL");
		FILE_EXTENSION_ASSOCIATIONS.put("psql");
		FILE_EXTENSION_ASSOCIATIONS.put("sql");
		FILE_EXTENSION_ASSOCIATIONS.put("SQL");
		FILE_EXTENSION_ASSOCIATIONS.put("data.sql");
		FILE_EXTENSION_ASSOCIATIONS.put("spc.sql");
		FILE_EXTENSION_ASSOCIATIONS.put("spoc.sql");
		FILE_EXTENSION_ASSOCIATIONS.put("sproc.sql");
		FILE_EXTENSION_ASSOCIATIONS.put("udf.sql");
		FILE_EXTENSION_ASSOCIATIONS.put("fun");
		FILE_EXTENSION_ASSOCIATIONS.put("sig");
		FILE_EXTENSION_ASSOCIATIONS.put("sml");
		FILE_EXTENSION_ASSOCIATIONS.put("swift");
		FILE_EXTENSION_ASSOCIATIONS.put("itk");
		FILE_EXTENSION_ASSOCIATIONS.put("tcl");
		FILE_EXTENSION_ASSOCIATIONS.put("tk");
		FILE_EXTENSION_ASSOCIATIONS.put("met");
		FILE_EXTENSION_ASSOCIATIONS.put("mth");
		FILE_EXTENSION_ASSOCIATIONS.put("tss");
		FILE_EXTENSION_ASSOCIATIONS.put("ts");
		FILE_EXTENSION_ASSOCIATIONS.put("mat");
		FILE_EXTENSION_ASSOCIATIONS.put("prefab");
		FILE_EXTENSION_ASSOCIATIONS.put("vala");
		FILE_EXTENSION_ASSOCIATIONS.put("vapi");
		FILE_EXTENSION_ASSOCIATIONS.put("vm");
		FILE_EXTENSION_ASSOCIATIONS.put("sv");
		FILE_EXTENSION_ASSOCIATIONS.put("svh");
		FILE_EXTENSION_ASSOCIATIONS.put("v");
		FILE_EXTENSION_ASSOCIATIONS.put("VHD");
		FILE_EXTENSION_ASSOCIATIONS.put("vhd");
		FILE_EXTENSION_ASSOCIATIONS.put("vhdl");
		FILE_EXTENSION_ASSOCIATIONS.put("VHDL");
		FILE_EXTENSION_ASSOCIATIONS.put("vim");
		FILE_EXTENSION_ASSOCIATIONS.put("bas");
		FILE_EXTENSION_ASSOCIATIONS.put("cls");
		FILE_EXTENSION_ASSOCIATIONS.put("ctl");
		FILE_EXTENSION_ASSOCIATIONS.put("dsr");
		FILE_EXTENSION_ASSOCIATIONS.put("frm");
		FILE_EXTENSION_ASSOCIATIONS.put("VB");
		FILE_EXTENSION_ASSOCIATIONS.put("vb");
		FILE_EXTENSION_ASSOCIATIONS.put("VBA");
		FILE_EXTENSION_ASSOCIATIONS.put("vba");
		FILE_EXTENSION_ASSOCIATIONS.put("vbs");
		FILE_EXTENSION_ASSOCIATIONS.put("VBS");
		FILE_EXTENSION_ASSOCIATIONS.put("sca");
		FILE_EXTENSION_ASSOCIATIONS.put("SCA");
		FILE_EXTENSION_ASSOCIATIONS.put("component");
		FILE_EXTENSION_ASSOCIATIONS.put("page");
		FILE_EXTENSION_ASSOCIATIONS.put("mc");
		FILE_EXTENSION_ASSOCIATIONS.put("def");
		FILE_EXTENSION_ASSOCIATIONS.put("rc");
		FILE_EXTENSION_ASSOCIATIONS.put("rc2");
		FILE_EXTENSION_ASSOCIATIONS.put("wxi");
		FILE_EXTENSION_ASSOCIATIONS.put("wxs");
		FILE_EXTENSION_ASSOCIATIONS.put("wxl");
		FILE_EXTENSION_ASSOCIATIONS.put("xaml");
		FILE_EXTENSION_ASSOCIATIONS.put("prg");
		FILE_EXTENSION_ASSOCIATIONS.put("ch");
		FILE_EXTENSION_ASSOCIATIONS.put("XML");
		FILE_EXTENSION_ASSOCIATIONS.put("xml");
		FILE_EXTENSION_ASSOCIATIONS.put("xq");
		FILE_EXTENSION_ASSOCIATIONS.put("xquery");
		FILE_EXTENSION_ASSOCIATIONS.put("xsd");
		FILE_EXTENSION_ASSOCIATIONS.put("XSD");
		FILE_EXTENSION_ASSOCIATIONS.put("xsl");
		FILE_EXTENSION_ASSOCIATIONS.put("XSL");
		FILE_EXTENSION_ASSOCIATIONS.put("xslt");
		FILE_EXTENSION_ASSOCIATIONS.put("XSLT");
		FILE_EXTENSION_ASSOCIATIONS.put("y");
		FILE_EXTENSION_ASSOCIATIONS.put("yaml");
		FILE_EXTENSION_ASSOCIATIONS.put("yml");*/
	}

}
