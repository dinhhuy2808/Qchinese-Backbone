package com.elearning.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.elearning.dao.UserRankingDao;
import com.elearning.entity.UserRanking;
import com.elearning.entity.UserResult;
import com.elearning.model.Rank;
import com.elearning.model.RankingLevel;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRankingDaoImpl implements UserRankingDao {
	private final JdbcTemplate jdbcTemplate;

	@Override
	public int getTotalScoreBy(Long userId) {
		StringBuilder sql = new StringBuilder("");
		sql.append("select "+
						"sum(u.totalscore) total "+
					"from "+
					"	userresult u "+
					"where "+
					"    testlesson <> 0 "+
					"	and userid = ? "+
					"group by "+
					"	u.userid");
		return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
			return rs.getInt("total");
		}).get(0);
	}
	

	@Override
	public int getCurrentRankBy(Long userId) {
		StringBuilder sql = new StringBuilder("");
		
		sql.append("select " + 
				"	max(userresult.currentrank) currentrank, " + 
				"	userresult.userid " + 
				"from " + 
				"	( " + 
				"	select " + 
				"		count(distinct u.testlesson) validcount, " + 
				"		max(u.hsk) currentrank, " + 
				"		u.userid, " + 
				"		p2.promotechain " + 
				"	from " + 
				"		userresult u " + 
				"	left join promotesetting p2 on " + 
				"		u.hsk = p2.hsk " + 
				"	where " + 
				"		u.resulttype = 'TEST' " + 
				"		and testlesson <> 0 " + 
				"		and u.totalscore >= p2.scorepertest " + 
				"		and u.userid = ? " + 
				"	group by " + 
				"		u.hsk, " + 
				"		userid " + 
				"	order by " + 
				"		userid, " + 
				"		u.hsk, " + 
				"		testlesson ) userresult " + 
				"where " + 
				"	userresult.validcount >= userresult.promotechain " + 
				"group by " + 
				"	userresult.userid");
		return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
			return rs.getInt("currentrank");
		}).get(0);
	}

	@Override
	public boolean isEligibleForPromoteTesting(int userId, int currentHsk) {
		StringBuilder sql = new StringBuilder("");
		sql.append("select\r\n" + 
				"	1\r\n" + 
				"from\r\n" + 
				"	(\r\n" + 
				"	select\r\n" + 
				"		count(u.testlesson) totalexercises,\r\n" + 
				"		sum(u.totalscore) totalscore,\r\n" + 
				"		sum(u.wordamount) + IFNULL((\r\n" + 
				"		select\r\n" + 
				"			wordamount\r\n" + 
				"		from\r\n" + 
				"			userresult u\r\n" + 
				"		where\r\n" + 
				"			u.resulttype = 'QUIZ'\r\n" + 
				"			and testlesson = 0\r\n" + 
				"			and userid = ?\r\n" + 
				"			and hsk = ?\r\n" + 
				"		group by\r\n" + 
				"			u.userid),0) totalwords,\r\n" + 
				"		u.hsk\r\n" + 
				"	from\r\n" + 
				"		userresult u\r\n" + 
				"	where\r\n" + 
				"		u.resulttype = 'QUIZ'\r\n" + 
				"		and testlesson <> 0\r\n" + 
				"		and userid = ?\r\n" + 
				"		and hsk = ?\r\n" + 
				"	group by\r\n" + 
				"		u.userid) summary\r\n" + 
				"inner join promotesetting setting on\r\n" + 
				"	setting.hsk = summary.hsk\r\n" + 
				"where\r\n" + 
				"	summary.totalwords >= setting.wordamount\r\n" + 
				"	and summary.totalscore >= setting.avgexercisescore");
		boolean results = !jdbcTemplate.queryForList(sql.toString(), userId, currentHsk, userId, currentHsk).isEmpty();
		return results;
	}

	@Override
	public boolean isEligibleForPromotion(int userId, int currentHsk) {
		StringBuilder sql = new StringBuilder("");
		sql.append("select"+
					"	case"+
					"		when count(setting.hsk) >= setting.promotechain then 'Y'"+
					"		else 'N'"+
					"	end as ispromoted"+
					"from"+
					"	("+
					"	select"+
					"		u.testlesson,"+
					"		u.totalscore,"+
					"		u.hsk"+
					"	from"+
					"		userresult u"+
					"	where"+
					"		u.resulttype = 'TEST'"+
					"		and testlesson <> 0"+
					"		and userid = ?"+
					"		and hsk = ? ) summary"+
					"left join promotesetting setting on"+
					"	setting.hsk = summary.hsk"+
					"where"+
					"	summary.totalscore >= setting.scorepertest"+
					"group by"+
					"	setting.hsk");
		boolean results = false;
		for(String result : jdbcTemplate.queryForList(sql.toString(),String.class, userId, currentHsk)) {
			if (result.equals("Y")) {
				results = true;
			}
		}
		return results;
	}

	@Override
	public List<UserResult> getTotalResultBy(Long userId) {
		List<UserResult> userResults = new ArrayList<UserResult>();
		StringBuilder sql = new StringBuilder("");
		sql.append("select " + 
				"	sum(total.totalscore) totalscore, " + 
				"	sum(total.totallistenscore) totallistenscore, " + 
				"	sum(total.totalreadingscore) totalreadingscore, " + 
				"	sum(total.wordamount) wordamount, " + 
				"	total.hsk " + 
				"from " + 
				"	( " + 
				"	select " + 
				"		max(totalscore) totalscore, max(totallistenscore) totallistenscore, max(totalreadingscore) totalreadingscore, max(wordamount) wordamount, hsk, testlesson " + 
				"	from " + 
				"		userresult u " + 
				"	where " + 
				"		testlesson <> 0 " + 
				"		and userid = ? " + 
				"	group by " + 
				"		hsk, testlesson " + 
				"	order by " + 
				"		hsk asc, testlesson asc ) total " + 
				"group by " + 
				"	hsk");
		userResults = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
			UserResult userResult = new UserResult();
			userResult.setHsk(rs.getInt("hsk"));
			userResult.setTotalScore(rs.getLong("totalscore"));
			userResult.setTotalListenScore(rs.getLong("totallistenscore"));
			userResult.setTotalReadingScore(rs.getLong("totalreadingscore"));
			userResult.setWordAmount(rs.getInt("wordamount"));
			return userResult;
		}, userId);
		return userResults;
	}

	@Override
	public List<Rank> getRankBoard() {
		List<Rank> ranks = new ArrayList<Rank>();
		StringBuilder sql = new StringBuilder("");
		sql.append("select " + 
				"	u.*, " + 
				"	u2.name userFullName " + 
				"from " + 
				"	userranking u " + 
				"join `user` u2 on " + 
				"	u.userid = u2.user_id " + 
				"order by " + 
				"	u.titleid desc,"
				+ " u.totalscore desc");
		ranks = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
			Rank rank = new Rank();
			rank.setTitleId(rs.getInt("titleid"));
			rank.setTotalScore(rs.getLong("totalscore"));
			rank.setUserId(rs.getLong("userid"));
			rank.setUserFullName(rs.getString("userFullName"));
			return rank;
		});
		ranks.forEach(rank -> {
			List<UserResult> userResult = getTotalResultBy(rank.getUserId());
			rank.setWordAmount(userResult.stream().mapToLong(result -> result.getWordAmount()).sum());
			rank.setHsk(userResult.isEmpty() ? 1 : userResult.get(userResult.size() - 1).getHsk());
		});
		return ranks;
	}
public static void main(String[] args) {
	StringBuilder sql = new StringBuilder("");
	sql.append("select ur.userid, ifnull(details.validcount, 0) validchain from ( " + 
			"select " + 
			"	u.userid , " + 
			"	count(distinct u.testlesson) validcount  " + 
			"from " + 
			"	userresult u " + 
			"left join promotesetting p2 on " + 
			"	u.hsk = p2.hsk " + 
			"where " + 
			"	u.resulttype = 'TEST' " + 
			"	and testlesson <> 0 " + 
			"	and u.totalscore >= p2.scorepertest " + 
			"	and userid in ( " + 
			"	select " + 
			"		userid " + 
			"	from " + 
			"		userranking " + 
			"	where " + 
			"		titleid = ?) " + 
			"	and u.hsk = ? " + 
			"group by " + 
			"	u.userid " + 
			"order by " + 
			"	u.userid, " + 
			"	u.hsk, " + 
			"	testlesson) details right join (select " + 
			"		userid " + 
			"	from " + 
			"		userranking " + 
			"	where " + 
			"		titleid = ?) ur on details.userid = ur.userid " + 
			"		order by ur.userid");
	System.out.println(sql.toString());
}
	@Override
	public Map<Integer, List<RankingLevel>> getCurrentRankLevel(int currentRank) {
		StringBuilder sql = new StringBuilder("");
		List<RankingLevel> rankingLevels = new ArrayList<RankingLevel>();
		sql.append("select " + 
				"	ur.userid, " + 
				"	ifnull(details.validcount, 0) validchain, " + 
				"	u2.name  " + 
				"from " + 
				"	( " + 
				"	select " + 
				"		u.userid , " + 
				"		count(distinct u.testlesson) validcount " + 
				"	from " + 
				"		userresult u " + 
				"	left join promotesetting p2 on " + 
				"		u.hsk = p2.hsk " + 
				"	where " + 
				"		u.resulttype = 'TEST' " + 
				"		and testlesson <> 0 " + 
				"		and u.totalscore >= p2.scorepertest " + 
				"		and userid in ( " + 
				"		select " + 
				"			userid " + 
				"		from " + 
				"			userranking " + 
				"		where " + 
				"			titleid = ?) " + 
				"		and u.hsk = ? " + 
				"	group by " + 
				"		u.userid " + 
				"	order by " + 
				"		u.userid, " + 
				"		u.hsk, " + 
				"		testlesson) details " + 
				"right join ( " + 
				"	select " + 
				"		userid " + 
				"	from " + 
				"		userranking " + 
				"	where " + 
				"		titleid = ?) ur on " + 
				"	details.userid = ur.userid " + 
				"	left join `user` u2 on ur.userid = u2.user_id  " + 
				"order by " + 
				"	ur.userid " + 
				"");
		jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
			RankingLevel rankingLevel = new RankingLevel();
			rankingLevel.setUserId(rs.getLong("userid"));
			rankingLevel.setValidChain(rs.getInt("validchain"));
			rankingLevel.setName(rs.getString("name"));
			if (rankingLevel.getValidChain() == 0) {
				rankingLevel.setLevel(1);
			} else if (rankingLevel.getValidChain() == 1) {
				rankingLevel.setLevel(2);
			}else if (rankingLevel.getValidChain() == 2) {
				rankingLevel.setLevel(3);
			}
			return rankingLevel;
		},currentRank, currentRank+1, currentRank);
		return rankingLevels.stream().collect(Collectors.groupingBy(RankingLevel::getLevel));
	}
}
