package control;

public class Config {
	private int idConf;
	private String serverSou;
	private int port;
	private String userSou;
	private String passSou;
	private String dirSou;
	private String fieldName;
	private String delimeterSou;
	private String formatSou;
	private String serverDes;
	private String DBNameDes;
	private String useDes;
	private String passDes;

	public Config() {

	}

	public int getIdConf() {
		return idConf;
	}

	public void setIdConf(int idConf) {
		this.idConf = idConf;
	}

	public String getServerSou() {
		return serverSou;
	}

	public void setServerSou(String serverSou) {
		this.serverSou = serverSou;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserSou() {
		return userSou;
	}

	public void setUserSou(String userSou) {
		this.userSou = userSou;
	}

	public String getPassSou() {
		return passSou;
	}

	public void setPassSou(String passSou) {
		this.passSou = passSou;
	}

	public String getDirSou() {
		return dirSou;
	}

	public void setDirSou(String dirSou) {
		this.dirSou = dirSou;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDelimeterSou() {
		return delimeterSou;
	}

	public void setDelimeterSou(String delimeterSou) {
		this.delimeterSou = delimeterSou;
	}

	public String getFormatSou() {
		return formatSou;
	}

	public void setFormatSou(String formatSou) {
		this.formatSou = formatSou;
	}

	public String getServerDes() {
		return serverDes;
	}

	public void setServerDes(String serverDes) {
		this.serverDes = serverDes;
	}

	public String getDBNameDes() {
		return DBNameDes;
	}

	public void setDBNameDes(String dBNameDes) {
		DBNameDes = dBNameDes;
	}

	public String getUseDes() {
		return useDes;
	}

	public void setUseDes(String useDes) {
		this.useDes = useDes;
	}

	public String getPassDes() {
		return passDes;
	}

	public void setPassDes(String passDes) {
		this.passDes = passDes;
	}

	@Override
	public String toString() {
		return "Config [idConf=" + idConf + ", serverSou=" + serverSou + ", port=" + port + ", userSou=" + userSou
				+ ", passSou=" + passSou + ", dirSou=" + dirSou + ", fieldName=" + fieldName + ", delimeterSou="
				+ delimeterSou + ", formatSou=" + formatSou + ", serverDes=" + serverDes + ", DBNameDes=" + DBNameDes
				+ ", useDes=" + useDes + ", passDes=" + passDes + "]";
	}

}
