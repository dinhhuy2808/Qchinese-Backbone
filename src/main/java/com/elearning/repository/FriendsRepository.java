package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elearning.entity.Friends;
import com.elearning.entity.User;
import com.elearning.entity.UserResult;
import com.elearning.model.FriendsResponse;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {
	List<Friends> findByUserIdAndIsAccepped(Long userId, boolean isAccepped);

	Long countByUserIdAndFriendId(Long userId, Long friendId);

	List<Friends> findByUserIdAndFriendIdAndIsAccepped(Long userId, Long friendId, boolean isAccepped);

	@Query(value = "select :userId as userId, r.roomkey as roomKey, r.roomname as roomName, u.avatar as avatar,"
			+ " r.unread as unRead, u.userkey as userKey, 1 as isAccept  " + "from rooms r "
			+ "left join friends f on r.userid = f.userid and r.friendid = f.friendid "
			+ "join `user` u on r.friendid = u.user_id  " + "where r.userid = :userId "
			+ "and f.isaccepped = 1", nativeQuery = true)
	List<FriendsResponse> getFriendsDetail(Long userId);

	@Modifying
	@Query(value = "INSERT INTO friends\r\n" + "(userid, friendid, friendnickname, isaccepped, createddate)\r\n"
			+ "VALUES(:userid, :friendid, (select name from `user` where user_id = :friendid), :isAccepped, sysdate())", nativeQuery = true)
	void insertFriends(@Param(value = "userid") Long userId, @Param(value = "friendid") Long friendId,
			@Param(value = "friendid") boolean isAccepped);

	@Modifying
	@Query(value = "INSERT INTO rooms " + "(userid, friendid, isgroup, roomname, roomkey, createddate, unread) "
			+ "VALUES(:userid, :friendid, 0, (select name from `user` where user_id = :userid), :roomKey, sysdate(), 0)", nativeQuery = true)
	void insertRooms(@Param(value = "userid") Long userId, @Param(value = "friendid") Long friendId,
			@Param(value = "roomKey") String roomKey);

	@Query(value = "select f1.friendid as userId," + " NULL as roomKey," + " f1.friendnickname as roomName,"
			+ " u.avatar as avatar," + " NULL as unread," + " u.userkey as userkey," + " false as isAccept"
			+ " from `user` u " + "right join (select f.friendid, f.friendnickname from friends f  "
			+ "left join `user` u2 on f.userid = u2.user_id  "
			+ "where f.userid = ? and f.isaccepped = 0) f1 on u.user_id = f1.friendid", nativeQuery = true)
	List<FriendsResponse> getFriendRequest(Long userId);

	boolean existsFriendsByUserIdAndFriendId(Long userId, Long friendId);
}
