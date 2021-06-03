package com.elearning.model;

import java.util.List;

public class GameContent {
	private List<GameMultiChoiceFormat> round1;
	private List<GameMultiChoiceFormat> round2;
	private GameMatchFormat round3;
	private GameTypingFormat round4;
	
	public GameContent(List<GameMultiChoiceFormat> round1, List<GameMultiChoiceFormat> round2,
			GameMatchFormat round3, GameTypingFormat round4) {
		this.round1 = round1;
		this.round2 = round2;
		this.round3 = round3;
		this.round4 = round4;
	}
	public List<GameMultiChoiceFormat> getRound1() {
		return round1;
	}
	public void setRound1(List<GameMultiChoiceFormat> round1) {
		this.round1 = round1;
	}
	public List<GameMultiChoiceFormat> getRound2() {
		return round2;
	}
	public void setRound2(List<GameMultiChoiceFormat> round2) {
		this.round2 = round2;
	}
	public GameMatchFormat getRound3() {
		return round3;
	}
	public void setRound3(GameMatchFormat round3) {
		this.round3 = round3;
	}
	public GameTypingFormat getRound4() {
		return round4;
	}
	public void setRound4(GameTypingFormat round4) {
		this.round4 = round4;
	}
	
}
