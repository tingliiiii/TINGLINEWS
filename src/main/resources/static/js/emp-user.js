// 從後端抓資料給 DataTable
const fetchData = async (uri) => {
	const url = `http://localhost:8080/tinglinews${uri}`;
	try {
		const response = await fetch(url); // 等待 fetch 請求完成
		const { state, message, data } = await response.json(); // 等待回應本文內容
		console.log(state, message, data);
		return data;
	} catch (e) {
		console.error(e);
		return [];
	}
};

// 權限選項
const loadAuthorityOptions = async () => {
	try {
		const data = await fetchData('/emp/authority');
		const select = $('#authority');

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

const handleUpdateAuthority = async (event) => {

	const userId = $('#userId').val();
	const formData = {
		userId: userId,
		authorityId: $('#authority').val()
	};

	try {
		const response = await fetch(`http://localhost:8080/tinglinews/emp/authority/${userId}`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData)
		});

		const { state, message, data } = await response.json();
		console.log(state, message, data);

		if (state) {
			sessionStorage.setItem('authrotyName', data.authority.authorityName);
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

	const userName = sessionStorage.getItem('userName');
    const authrotyName = sessionStorage.getItem('authrotyName');

    $('#user').html(
        `<p>${authrotyName}&ensp;${userName}&ensp;已登入</p>`
    );

	// 載入權限選項
	loadAuthorityOptions();

	// 使用者管理表格
	const table = $('#user-table').DataTable({
		ajax: async (data, callback) => {
			// 從伺服器獲取數據
			const result = await fetchData('/emp/user');
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

	/** 檢查表格排序
		table.on('order.dt', function() {
			console.log('Current order:', table.order());
		});
	 */
	// fetchAndRenderData('/emp/user', 'user-table-body', renderUser);
	/*
	$('#user-table').on('click', async(event) => {
		console.log(event);
		// 處理事件
		// await handleEvent(event, 'update-user-button', handleUpdateUser);
		await handleEvent(event, 'delete-user-btn', handleDeleteUser);
	});
	*/

	$('#user-table').on('click', '.delete-user-btn', async (event) => {
		/* 'span.delete-user-btn',
		const data = table.row($(this).parents('tr')).data();
		console.log(data);
		const userId = data.userId;
		handleDeleteUser(userId);
		*/
		// console.log(event);
		// console.log($(this).parents('tr'));

		if (!$(event.target).hasClass('delete-user-btn')) {
			return;
		}
		const row = $(event.target).closest('tr');
		const userId = $(event.target).data('id');
		await handleDeleteUser(userId, row);
	});

	// 點兩下權限欄位
	$('#user-table').on('dblclick', '.authority', (event) => {
		const span = $(event.target);
		const row = span.closest('tr');
		const userData = table.row(row).data();

		console.log(userData);

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
		console.log('按下刪除：' + userId);
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

		const url = `http://localhost:8080/tinglinews/emp/user/${userId}`;
		const response = await fetch(url, { method: 'DELETE' }); // 等待 fetch 請求完成
		const { state, message, data } = await response.json(); // 等待回應本文內容
		console.log(state, message, data);

		if (state) {
			// 更新 user list
			// $('#user-table').DataTable().ajax.reload();
			Swal.fire(message, '', 'success');
			// console.log($(this));
			// 直接從 DataTable 中刪除該行並重新繪製表格
			table.row(row).remove().draw();
			// table.ajax.reload();
		} else {
			Swal.fire(message, '', 'error');
		}
	};



});
