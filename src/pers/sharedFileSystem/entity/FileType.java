package pers.sharedFileSystem.entity;

import java.io.Serializable;

/**
 * 文件类型枚取
 * 
 * @author buaashuai
 */
public enum FileType implements Serializable {
	/**
	 * 所有文件类型
	 */
	ANY("0000000000"),
	/**
	 * JEPG.jpg
	 */
	JPEG("ffd8ffe000104a464946"),

	/**
	 * PNG .png
	 */
	PNG("89504e470d0a1a0a0000"),

	/**
	 * GIF.gif
	 */
	GIF("474946383961"),

	/**
	 * TIFF.tif
	 */
	TIFF("49492a00227105008037"),

	/**
	 * Windows Bitmap.16色位图(bmp)
	 */
	BMP16("424d228c010000000000"),
	/**
	 * Windows Bitmap.24色位图(bmp)
	 */
	BMP24("424d8240090000000000"),
	/**
	 * Windows Bitmap.256色位图(bmp)
	 */
	BMP256("424d8e1b030000000000"),
	/**
	 * CAD.dwg
	 */
	DWG("41433130313500000000"),

	/**
	 * Adobe Photoshop.psd
	 */
	PSD("38425053000100000000"),
	/**
	 * PS.ps
	 */
	PS("252150532D41646F6265"),

	/**
	 * Rich Text Format.rtf
	 */
	RTF("7b5c727466315c616e73"),

	/**
	 * XML.xml
	 */
	XML("3c3f786d6c2076657273"),
	/**
	 * TXT.txt
	 */
	TXT("3c3f786d6c207665727"),
	/**
	 * SQL.sql
	 */
	SQL("494e5345525420494e54"),
	/**
	 * JAVA.java
	 */
	JAVA("7061636b616765207765"),
	/**
	 * HTML.html
	 */
	HTML("3c21444f435459504520"),
	/**
	 * HTM.htm
	 */
	HTM("3c21646f637479706520"),
	/**
	 * CSS.css
	 */
	CSS("48544d4c207b0d0a0942"),
	/**
	 * JS.js
	 */
	JS("696b2e71623d696b2e71"),
	/**
	 * Email [thorough only].eml
	 */
	EML("46726f6d3a203d3f6762"),

	/**
	 * Outlook Express.
	 */
	DBX("CFAD12FEC5FD746F"),

	/**
	 * Outlook (pst).
	 */
	PST("2142444E"),

	/**
	 * MS Word/Excel.
	 */
	DOC("d0cf11e0a1b11ae10000"),
	/**
	 * VSD.vsd
	 */
	VSD("d0cf11e0a1b11ae10000"),
	/**
	 * MS Access.mdb
	 */
	MDB("5374616E64617264204A"),

	/**
	 * WordPerfect.
	 */
	WPD("FF575043"),

	/**
	 * Postscript.
	 */
	EPS("252150532D41646F6265"),

	/**
	 * Adobe Acrobat.pdf
	 */
	PDF("255044462d312e"),
	/**
	 * BAT.bat
	 */
	BAT("406563686f206f66660d"),
	/**
	 * GZ.gz
	 */
	GZ("1f8b0800000000000000"),
	/**
	 * DOCX.docx
	 */
	DOCX("504b0304140006000800"),
	/**
	 * WPS.wps
	 */
	WPS("d0cf11e0a1b11ae10000"),
	/**
	 * CLASS.class
	 */
	CLASS("cafebabe0000002e0041"),
	/**
	 * CHM.chm
	 */
	CHM("49545346030000006000"),
	/**
	 * Quicken.
	 */
	QDF("AC9EBD8F"),

	/**
	 * Windows Password.
	 */
	PWL("E3828596"),

	/**
	 * ZIP Archive.
	 */
	ZIP("504b0304140000000800"),

	/**
	 * RAR Archive.
	 */
	RAR("526172211a0700cf9073"),
	/**
	 * INI.ini
	 */
	INI("235468697320636f6e66"),
	/**
	 * JAR.jar
	 */
	JAR("504b03040a0000000000"),
	/**
	 * EXE.exe
	 */
	EXE("4d5a9000030000000400"),
	/**
	 * JSP.jsp
	 */
	JSP("3c25402070616765206c"),
	/**
	 * MF.mf
	 */
	MF("4d616e69666573742d56"),
	/**
	 * Wave.
	 */
	WAV("52494646e27807005741"),

	/**
	 * AVI.
	 */
	AVI("52494646d07d60074156"),

	/**
	 * Real Audio.
	 */
	RAM("2E7261FD"),

	/**
	 * Real Media.rmvb/rm相同
	 */
	RM("2e524d46000000120001"),

	/**
	 * MPEG (mpg).
	 */
	MPG("000001ba210001000180"),
	/**
	 * WMV .wmv
	 */
	WMV("3026b2758e66cf11a6d9"),
	/**
	 * Quicktime.
	 */
	MOV("6D6F6F76"),

	/**
	 * Windows Media.
	 */
	ASF("3026B2758E66CF11"),

	/**
	 * MIDI.
	 */
	MID("4d546864000000060001"),
	/**
	 * .properties
	 */
	PROPERTIES("6c6f67346a2e726f6f74"),
	/**
	 * FLV.
	 */
	FLV("464c5601050000000900"),
	/**
	 * MP4.
	 */
	MP4("0000001866747970"),
	/**
	 * MP3.
	 */
	MP3("49443303000000002176");

	private String value = "";

	/**
	 * Constructor.
	 * 
	 * @param type
	 */
	FileType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
