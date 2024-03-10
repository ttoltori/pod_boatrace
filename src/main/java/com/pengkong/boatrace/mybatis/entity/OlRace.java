package com.pengkong.boatrace.mybatis.entity;

import com.pengkong.boatrace.service.manager.JyoManager;

public class OlRace {
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.ymd
	 * @mbg.generated
	 */
	private String ymd;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.jyocd
	 * @mbg.generated
	 */
	private String jyocd;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.raceno
	 * @mbg.generated
	 */
	private Integer raceno;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.sime
	 * @mbg.generated
	 */
	private String sime;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.grade
	 * @mbg.generated
	 */
	private String grade;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.timezone
	 * @mbg.generated
	 */
	private String timezone;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.turn
	 * @mbg.generated
	 */
	private String turn;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.fixedentrance
	 * @mbg.generated
	 */
	private String fixedentrance;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.racetype
	 * @mbg.generated
	 */
	private String racetype;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.wakulevellist
	 * @mbg.generated
	 */
	private String wakulevellist;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.alevelcount
	 * @mbg.generated
	 */
	private Integer alevelcount;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.femalecount
	 * @mbg.generated
	 */
	private Integer femalecount;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.com_predict
	 * @mbg.generated
	 */
	private String comPredict;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.com_confidence
	 * @mbg.generated
	 */
	private Integer comConfidence;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.ol_race.status
	 * @mbg.generated
	 */
	private Integer status;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.ymd
	 * @return  the value of public.ol_race.ymd
	 * @mbg.generated
	 */
	public String getYmd() {
		return ymd;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.ymd
	 * @param ymd  the value for public.ol_race.ymd
	 * @mbg.generated
	 */
	public void setYmd(String ymd) {
		this.ymd = ymd;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.jyocd
	 * @return  the value of public.ol_race.jyocd
	 * @mbg.generated
	 */
	public String getJyocd() {
		return jyocd;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.jyocd
	 * @param jyocd  the value for public.ol_race.jyocd
	 * @mbg.generated
	 */
	public void setJyocd(String jyocd) {
		this.jyocd = jyocd;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.raceno
	 * @return  the value of public.ol_race.raceno
	 * @mbg.generated
	 */
	public Integer getRaceno() {
		return raceno;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.raceno
	 * @param raceno  the value for public.ol_race.raceno
	 * @mbg.generated
	 */
	public void setRaceno(Integer raceno) {
		this.raceno = raceno;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.sime
	 * @return  the value of public.ol_race.sime
	 * @mbg.generated
	 */
	public String getSime() {
		return sime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.sime
	 * @param sime  the value for public.ol_race.sime
	 * @mbg.generated
	 */
	public void setSime(String sime) {
		this.sime = sime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.grade
	 * @return  the value of public.ol_race.grade
	 * @mbg.generated
	 */
	public String getGrade() {
		return grade;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.grade
	 * @param grade  the value for public.ol_race.grade
	 * @mbg.generated
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.timezone
	 * @return  the value of public.ol_race.timezone
	 * @mbg.generated
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.timezone
	 * @param timezone  the value for public.ol_race.timezone
	 * @mbg.generated
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.turn
	 * @return  the value of public.ol_race.turn
	 * @mbg.generated
	 */
	public String getTurn() {
		return turn;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.turn
	 * @param turn  the value for public.ol_race.turn
	 * @mbg.generated
	 */
	public void setTurn(String turn) {
		this.turn = turn;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.fixedentrance
	 * @return  the value of public.ol_race.fixedentrance
	 * @mbg.generated
	 */
	public String getFixedentrance() {
		return fixedentrance;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.fixedentrance
	 * @param fixedentrance  the value for public.ol_race.fixedentrance
	 * @mbg.generated
	 */
	public void setFixedentrance(String fixedentrance) {
		this.fixedentrance = fixedentrance;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.racetype
	 * @return  the value of public.ol_race.racetype
	 * @mbg.generated
	 */
	public String getRacetype() {
		return racetype;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.racetype
	 * @param racetype  the value for public.ol_race.racetype
	 * @mbg.generated
	 */
	public void setRacetype(String racetype) {
		this.racetype = racetype;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.wakulevellist
	 * @return  the value of public.ol_race.wakulevellist
	 * @mbg.generated
	 */
	public String getWakulevellist() {
		return wakulevellist;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.wakulevellist
	 * @param wakulevellist  the value for public.ol_race.wakulevellist
	 * @mbg.generated
	 */
	public void setWakulevellist(String wakulevellist) {
		this.wakulevellist = wakulevellist;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.alevelcount
	 * @return  the value of public.ol_race.alevelcount
	 * @mbg.generated
	 */
	public Integer getAlevelcount() {
		return alevelcount;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.alevelcount
	 * @param alevelcount  the value for public.ol_race.alevelcount
	 * @mbg.generated
	 */
	public void setAlevelcount(Integer alevelcount) {
		this.alevelcount = alevelcount;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.femalecount
	 * @return  the value of public.ol_race.femalecount
	 * @mbg.generated
	 */
	public Integer getFemalecount() {
		return femalecount;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.femalecount
	 * @param femalecount  the value for public.ol_race.femalecount
	 * @mbg.generated
	 */
	public void setFemalecount(Integer femalecount) {
		this.femalecount = femalecount;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.com_predict
	 * @return  the value of public.ol_race.com_predict
	 * @mbg.generated
	 */
	public String getComPredict() {
		return comPredict;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.com_predict
	 * @param comPredict  the value for public.ol_race.com_predict
	 * @mbg.generated
	 */
	public void setComPredict(String comPredict) {
		this.comPredict = comPredict;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.com_confidence
	 * @return  the value of public.ol_race.com_confidence
	 * @mbg.generated
	 */
	public Integer getComConfidence() {
		return comConfidence;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.com_confidence
	 * @param comConfidence  the value for public.ol_race.com_confidence
	 * @mbg.generated
	 */
	public void setComConfidence(Integer comConfidence) {
		this.comConfidence = comConfidence;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.ol_race.status
	 * @return  the value of public.ol_race.status
	 * @mbg.generated
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.ol_race.status
	 * @param status  the value for public.ol_race.status
	 * @mbg.generated
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	public String toString() {
		return "race{" + String.join(",", ymd, JyoManager.getJyoName(jyocd), "("+jyocd+")", raceno.toString(), sime) + "}";
	}
}