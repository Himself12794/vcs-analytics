package com.cisco.dft.sdk.vcs.util;

import java.util.HashMap;
import java.util.Map;

import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;

public interface FileExtensionMapping {

	@SuppressWarnings("serial")
	static final Map<String, Language> FILE_EXTENSION_ASSOCIATIONS = new HashMap<String, Language>() { 
		
		{
			
	
			this.put("java", Language.JAVA);
			this.put("cs", Language.CSHARP);
			this.put("c", Language.C);
			this.put("cc", Language.CPP);
			this.put("cpp", Language.CPP);
			this.put("cxx", Language.CPP);
			this.put("h", Language.C);
			this.put("hpp", Language.CPP);
			this.put("hxx", Language.CPP);
			this.put("js", Language.JAVASCRIPT);
			this.put("jsp", Language.JSP);
			this.put("py", Language.PYTHON);
			this.put("pyw", Language.PYTHON);
			this.put("lua", Language.LUA);
			this.put("html", Language.HTML);
			this.put("xhtml", Language.HTML);
			this.put("shtml", Language.HTML);
			this.put("php", Language.PHP);
			this.put("php3", Language.PHP);
			this.put("phpt", Language.PHP);
			this.put("phtml", Language.PHP);
			this.put("xml", Language.XML);
			this.put("css", Language.CSS);
			this.put("yml", Language.YAML);
			this.put("abap", Language.ABAP);
			this.put("as", Language.ACTIONSCRIPT);
			this.put("ada", Language.ADA); 
			this.put("adb", Language.ADA); 
			this.put("ads", Language.ADA); 
			this.put("pad", Language.ADA);
			this.put("adso", Language.ADSO_IDSM);
			this.put("ample", Language.AMPLE); 
			this.put("dofile", Language.AMPLE); 
			this.put("startup", Language.AMPLE);
			this.put("build.xml", Language.ANT);
			this.put("trigger", Language.APEX_TRIGGER);
			this.put("ino", Language.ARDUINO_SKETCH); 
			this.put("pde", Language.ARDUINO_SKETCH);
			this.put("asa", Language.ASP); 
			this.put("asp", Language.ASP);
			this.put("asax", Language.ASPdotNET); 
			this.put("ascx", Language.ASPdotNET); 
			this.put("asmx", Language.ASPdotNET); 
			this.put("aspx", Language.ASPdotNET); 
			this.put("config", Language.ASPdotNET); 
			this.put("master", Language.ASPdotNET); 
			this.put("sitemap", Language.ASPdotNET);
			this.put("webinfo", Language.ASPdotNET);
			this.put("asm", Language.ASSEMBLY);
			this.put("s", Language.ASSEMBLY);
			this.put("S", Language.ASSEMBLY);
			this.put("ahk", Language.AUTOHOTKEY);
			this.put("awk", Language.AWK);
			this.put("bash", Language.BOURNE_AGAIN_SHELL);
			this.put("sh", Language.BOURNE_AGAIN);
			this.put("c", Language.C);
			this.put("ec", Language.C);
			this.put("pgc", Language.C);
			/*this.put("csh");
			this.put("tcsh");
			this.put("cs");
			this.put("C");
			this.put("c++", Language.C);
			this.put("cc");
			this.put("cpp");
			this.put("cxx");
			this.put("pcc");
			this.put("h");
			this.put("H");
			this.put("hh");
			this.put("hpp");
			this.put("ccs");
			this.put("clj");
			this.put("cljs");
			this.put("cmake");
			this.put("CMakeLists.txt");
			this.put("cbl");
			this.put("CBL");
			this.put("cob");
			this.put("COB");
			this.put("coffee");
			this.put("cfm");
			this.put("cfc");
			this.put("css");
			this.put("cu");
			this.put("pyx");
			this.put("d");
			this.put("da");
			this.put("dart");
			this.put("diff");
			this.put("dita");
			this.put("bat");
			this.put("BAT");
			this.put("btm");
			this.put("BTM");
			this.put("cmd");
			this.put("CMD");
			this.put("dtd");
			this.put("ecpp");
			this.put("ex");
			this.put("exs");
			this.put("ERB");
			this.put("erb");
			this.put("erl");
			this.put("hrl");
			this.put("exp");
			this.put("fs");
			this.put("fsi");
			this.put("focexec");
			this.put("f");
			this.put("F");
			this.put("f77");
			this.put("F77");
			this.put("for");
			this.put("FOR");
			this.put("FTN");
			this.put("ftn");
			this.put("pfo");
			this.put("f90");
			this.put("F90");
			this.put("f95");
			this.put("F95");
			this.put("go");
			this.put("gsp");
			this.put("gant");
			this.put("gradle");
			this.put("groovy");
			this.put("haml");
			this.put("handlebars");
			this.put("hbs");
			this.put("hb");
			this.put("hs");
			this.put("lhs");
			this.put("cg");
			this.put("cginc");
			this.put("shader");
			this.put("htm");
			this.put("html");
			this.put("idl");
			this.put("pro");
			this.put("ism");
			this.put("java");
			this.put("js");
			this.put("jsf");
			this.put("xhtml");
			this.put("jcl");
			this.put("json");
			this.put("jsp");
			this.put("jspf");
			this.put("ksc");
			this.put("ksh");
			this.put("kt");
			this.put("less");
			this.put("l");
			this.put("el");
			this.put("lisp");
			this.put("lsp");
			this.put("sc");
			this.put("jl");
			this.put("cl");
			this.put("oscript");
			this.put("lua");
			this.put("ac");
			this.put("m4");
			this.put("am");
			this.put("gnumakefile");
			this.put("Gnumakefile");
			this.put("makefile");
			this.put("Makefile");
			this.put("m");
			this.put("pom");
			this.put("pom.xml");
			this.put("i3");
			this.put("ig");
			this.put("m3");
			this.put("mg");
			this.put("csproj");
			this.put("vbproj");
			this.put("vcproj");
			this.put("wdproj");
			this.put("wixproj");
			this.put("mps");
			this.put("m");
			this.put("mustache");
			this.put("mxml");
			this.put("build");
			this.put("dmap");
			this.put("m");
			this.put("mm");
			this.put("ml");
			this.put("mli");
			this.put("mll");
			this.put("mly");
			this.put("fmt");
			this.put("rex");
			this.put("dpr");
			this.put("p");
			this.put("pas");
			this.put("pp");
			this.put("pcl");
			this.put("ses");
			this.put("perl");
			this.put("plh");
			this.put("plx");
			this.put("pm");
			this.put("PL");
			this.put("pl");
			this.put("php");
			this.put("php3");
			this.put("php4");
			this.put("php5");
			this.put("inc");
			this.put("pig");
			this.put("pl1");
			this.put("ps1");
			this.put("P");
			this.put("proto");
			this.put("purs");
			this.put("py");
			this.put("qml");
			this.put("R");
			this.put("rkt");
			this.put("rktl");
			this.put("sch");
			this.put("scm");
			this.put("scrbl");
			this.put("ss");
			this.put("cshtml");
			this.put("rexx");
			this.put("robot");
			this.put("tsv");
			this.put("rake");
			this.put("rb");
			this.put("rhtml");
			this.put("rs");
			this.put("sas");
			this.put("sass");
			this.put("scss");
			this.put("scala");
			this.put("sed");
			this.put("il");
			this.put("ils");
			this.put("smarty");
			this.put("tpl");
			this.put("sbl");
			this.put("SBL");
			this.put("psql");
			this.put("sql");
			this.put("SQL");
			this.put("data.sql");
			this.put("spc.sql");
			this.put("spoc.sql");
			this.put("sproc.sql");
			this.put("udf.sql");
			this.put("fun");
			this.put("sig");
			this.put("sml");
			this.put("swift");
			this.put("itk");
			this.put("tcl");
			this.put("tk");
			this.put("met");
			this.put("mth");
			this.put("tss");
			this.put("ts");
			this.put("mat");
			this.put("prefab");
			this.put("vala");
			this.put("vapi");
			this.put("vm");
			this.put("sv");
			this.put("svh");
			this.put("v");
			this.put("VHD");
			this.put("vhd");
			this.put("vhdl");
			this.put("VHDL");
			this.put("vim");
			this.put("bas");
			this.put("cls");
			this.put("ctl");
			this.put("dsr");
			this.put("frm");
			this.put("VB");
			this.put("vb");
			this.put("VBA");
			this.put("vba");
			this.put("vbs");
			this.put("VBS");
			this.put("sca");
			this.put("SCA");
			this.put("component");
			this.put("page");
			this.put("mc");
			this.put("def");
			this.put("rc");
			this.put("rc2");
			this.put("wxi");
			this.put("wxs");
			this.put("wxl");
			this.put("xaml");
			this.put("prg");
			this.put("ch");
			this.put("XML");
			this.put("xml");
			this.put("xq");
			this.put("xquery");
			this.put("xsd");
			this.put("XSD");
			this.put("xsl");
			this.put("XSL");
			this.put("xslt");
			this.put("XSLT");
			this.put("y");
			this.put("yaml");
			this.put("yml");*/
		}
	};

}
