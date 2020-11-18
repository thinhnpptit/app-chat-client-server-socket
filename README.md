# app-chat-client-server-socket
BTL LTM send text + file


## How to work?
Chức năng dự kiến của ứng dụng:
public chat 
private chat
gửi file
vừa gửi file và chat(đa luồng)
hiện danh sách client online

Server gồm 2 thread text và file trên port lần lượt là 8080 và 6666
Các client gồm 2 thread tương ứng 

Thread text để cập nhật danh sách các client và gửi tin nhắn dạng text
Thread file để gửi tin nhắn dạng file

*Client A khi muốn gửi tin nhắn cho các  client khác:
- Chuỗi kèm theo tin nhắn được gửi cho server
Chuỗi gửi đi: text$nguoigui@nguoinhan@tinnhan
- Server xử lý và gửi lại cho các client khác client
	xử lý để trích ra nguoinhan và nguoigui
	Nếu nguoinhan là Everyone, server gửi cho tất cả client
	Nếu nguoinhan khác Everyone, server chỉ gửi cho nguoinhan và 			nguoigui
Chuỗi sau khi xl : text$tinnhan
- Client nhận chuỗi từ server, xử lý và xuất ra giao diện
xuất tinnhan ra giao diện

*Khi có client mới kết nối :
- Server gửi chuỗi để các client cập nhật danh sách trên giao diện
Chuỗi gửi đi : newuser$user1@user2@user3@....
- Các client nhận và xử lý chuỗi lấy ra danh sách user rồi tự cập nhật
 

*Khi có client ngắt kết nối :
- Server gửi chuỗi để đóng client đó các client cập nhật danh sách trên giao diện
Chuỗi gửi đi : close$closedUser# user1@user2@....
- Các client nhận và xử lý chuỗi 
tách ra user cần đóng:  closedUser
tách ra danh sách user để cập nhật lên giao diện

Lưu ý : client còn có thể gọi là user

*Cách xử lý chuỗi của client và server :
-Nhận biết chức năng để xử lý tương ứng, chức năng là phần đứng trước dấu $
vd : text$, file$, close$
- Thông tin phía sau có thể ngăn cách bằng các ký tự #, @
vd: close$closedUser#user1@user2@
text$sender@recipient@tinnhan

Lưu ý: Gửi file chưa tìm hiểu nên phương pháp gửi chuỗi có thể không áp dụng với chức năng gửi file
Ở các file connection ở server vẫn phải kiểm tra newuser và close để cập nhật trong Map đã lưu. Còn file connection bên client bỏ kiểm tra newuser và close đi cũng được?

