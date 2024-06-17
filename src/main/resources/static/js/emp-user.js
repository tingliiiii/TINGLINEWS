// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

// 從後端抓資料給 DataTable
const fetchData = async (uri) => {
	try {
		const response = await fetch(`http://${ip}:8080/tinglinews${uri}`); // 等待 fetch 請求完成
		const { state, message, data } = await response.json(); // 等待回應本文內容
		// console.log(state, message, data);
		return data;
	} catch (e) {
		console.error(e);
		return [];
	}
};

// 權限選項
const loadAuthorityOptions = async () => {
	try {
		const data = await fetchData('/admin/authorities');
		const select = $('#authority');
		select.empty(); // 清空現有選項

		data.forEach(authority => {
			const option = $('<option></option>');
			option.attr('value', authority.authorityId);
			option.text(authority.authorityName);
			select.append(option);
		});
	} catch (error) {
		console.error('Fetching data error:', error);
	}

};

const handleUpdateAuthority = async () => {

	const userId = $('#userId').val();
	const formData = {
		userId: userId,
		authorityId: $('#authority').val()
	};

	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/admin/users/${userId}/authority`, {
			method: 'PATCH',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData)
		});

		const { state, message, data } = await response.json();
		// console.log(state, message, data);

		if (state) {
			Swal.fire(message, '', 'success');
			setTimeout(() => {
				window.location.reload();
			}, 1000);
		} else {
			Swal.fire(message, '', 'error');
		}
	} catch (e) {
		console.error('Error updating authority:', e);
		Swal.fire('更新過程中發生錯誤', '', 'error');
	}
};


$(document).ready(() => {

	const data = JSON.parse(sessionStorage.getItem('userData'));

	if (!data) {
		window.location.replace('/tinglinews/emp/login.html');
		return;
	}

	const authorityId = data.authority.authorityId;
    if (authorityId < 1) {
        window.location.replace('/tinglinews/emp/login.html');
        return;
    }

	const userName = data.userName;
	const authorityName = data.authority.authorityName;

	$('#user').html(
		`<p>${authorityName}&ensp;${userName}&ensp;已登入</p>`
	);

	// 載入權限選項
	loadAuthorityOptions();

	// 使用者管理表格
	const table = $('#user-table').DataTable({
		ajax: async (data, callback) => {
			// 從伺服器獲取數據
			const result = await fetchData('/admin/users');
			// 將數據傳遞給 DataTables 以製作表格
			callback({
				data: result
			});
		},
		columns: [
			{ data: 'userId' },
			{ data: 'userName' },
			{ data: 'userEmail' },
			{
				data: 'authority',
				render: (data) =>
					`<span class="authority" data-id=${data.authorityId}>${data.authorityName}</span>`
			},
			{ data: 'registeredTime' },
			{
				data: 'userId',
				render: (data) =>
					`<button class="btn btn-close delete-user-btn" data-id=${data}></button>`
			}
		],
		order: [[0, 'desc']]
	});

	$('#user-table').on('click', '.delete-user-btn', async (event) => {
		/* 'span.delete-user-btn',
		const data = table.row($(this).parents('tr')).data();
		// console.log(data);
		const userId = data.userId;
		handleDeleteUser(userId);
		*/
		// // console.log(event);
		// // console.log($(this).parents('tr'));

		const row = $(event.target).closest('tr');
		const userId = $(event.target).data('id');
		await handleDeleteUser(userId, row);
	});

	// 點兩下權限欄位
	$('#user-table').on('dblclick', '.authority', (event) => {

		// 檢查使用者權限：只有主管或管理員可變更權限
		const authorityId = JSON.parse(sessionStorage.getItem('userData')).authority.authorityId;
		if (authorityId < 4) {
			Swal.fire('權限不足', '帳號權限有誤，請聯絡管理員', 'error');
			return;
		}

		const span = $(event.target);
		const row = span.closest('tr');
		const userData = table.row(row).data();

		// console.log(userData);

		// 填充 #edit-authority-form 表格欄位
		$('#userId').val(userData.userId);
		$('#userName').val(userData.userName);
		$('#userEmail').val(userData.userEmail);
		$('#authority').val(userData.authority.authorityId);

		// 顯示模態框
		$('#userModal').modal('show');
	});

	// 儲存變更按鈕事件處理
	$('#userModal').on('click', '.submit-btn', handleUpdateAuthority);


	// 處理用戶刪除事件
	const handleDeleteUser = async (userId, row) => {
		// console.log('按下刪除：' + userId);

		// 檢查使用者權限：只有管理員可刪除使用者
		const authorityId = JSON.parse(sessionStorage.getItem('userData')).authority.authorityId;
		if (authorityId < 5) {
			Swal.fire('權限不足', '帳號權限有誤，請聯絡管理員', 'error');
			return;
		}

		const result = await Swal.fire({
			title: '確定要刪除嗎？',
			text: '刪除後將無法恢復',
			icon: 'warning',
			showCancelButton: true,
			confirmButtonText: '確認刪除',
			cancelButtonText: '取消'
		});

		if (!result.isConfirmed) {
			Swal.fire('並未刪除', '', 'info');
			return;
		}

		try {
			const url = `http://${ip}:8080/tinglinews/admin/users/${userId}`;
			const response = await fetch(url, { method: 'DELETE' }); // 等待 fetch 請求完成
			const { state, message, data } = await response.json(); // 等待回應本文內容
			// console.log(state, message, data);

			if (state) {
				// 更新 user list
				// $('#user-table').DataTable().ajax.reload();
				Swal.fire(message, '', 'success');
				// // console.log($(this));
				// 直接從 DataTable 中刪除該行並重新繪製表格
				table.row(row).remove().draw();
				// table.ajax.reload();
			} else {
				Swal.fire(message, '', 'error');
			}
		} catch (e) {
			console.error('Error deleting user:', error);
			Swal.fire('刪除過程中發生錯誤', '', 'error');

		}
	};

	// 登出
	$('.logout-btn').on('click', () => {
		sessionStorage.clear();
		Swal.fire('登出成功', '', 'success');
		setTimeout(() => {
			window.location.replace('/tinglinews/emp/login.html');
		}, 1000);
	});


});
