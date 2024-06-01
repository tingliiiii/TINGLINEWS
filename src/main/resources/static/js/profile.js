// 從後端抓資料給 profile
const fetchData = async (userId) => {
	try {
		const response = await fetch(`http://localhost:8080/tinglinews/user/profile/${userId}`, {
			method: 'GET',
			credentials: 'include' // 確保請求包含 cookies
		});
		const { state, message, data } = await response.json(); // 等待回應本文內容
		console.log(state, message, data);

		$('#userId').val(data.userId);
		$('#userEmail').val(data.userEmail);
		$('#userName').val(data.userName);
		$('#gender').val(data.gender);
		$('#birth').val(data.birth);
		$('#phone').val(data.phone);

		renderSaved(data.savedList);
		renderDonated(data.donatedList);

	} catch (e) {
		console.error(e);
	}
}

// 收藏紀錄
const renderSaved = (data) => {

	const render = ({ savedId, news, savedTime }) => `
	<tr>
		<td>${savedId}</td>
		<td>${news.title}</td>
		<td>${news.publicTime}</td>
		<td>${savedTime}</td>
		<td>
			<button class="btn btn-close cancel-saved-btn" data-id="${savedId}"></button>
		</td>
	</tr>`;
	$('#saved-list-body').html(Array.isArray(data) ? data.map(render).join('') : render(data));
}

// 贊助紀錄
const renderDonated = (data) => {

	console.log(data);
	const render = ({ donatedId, frequency, amount, donatedTime, endTime, donateStatus }) => `
	<tr>
		<td>${donatedId}</td>
		<td>${frequency}</td>
		<td>${amount}</td>
		<td>${donatedTime}</td>
		<td>${endTime}</td>
		<td>${donateStatus}</td>
		<td>
			<button class="btn btn-close stop-donate-btn" data-id="${donatedId}"></button>
		</td>
	</tr>`;
	$('#donated-list-body').html(Array.isArray(data) ? data.map(render).join('') : render(data));
}

const handleSubmit = async (event) => {

	event.preventDefault();

	const formData = {
		userEmail: $('#userEmail').val(),
		userName: $('#userName').val(),
		gender: $('#gender').val(),
		birth: $('#birth').val(),
		phone: $('#phone').val()
	};
	await update(formData);
};

const update = async (formData) => {
	try {
		const response = await fetch('http://localhost:8080/tinglinews/user/update', {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData),
			// credentials: 'include' // 確保請求包含 cookies
		});

		const { state, message, data } = await response.json();
		console.log(state, message, data);

		if (state) {
			Swal.fire('更新成功', message, 'success');
		} else {
			Swal.fire('更新失敗！請稍後再試', message, 'warning');
		}
	} catch (error) {
		console.error('更新個人資訊錯誤：', error);
		Swal.fire('更新過程發生錯誤！請稍後再試', error, 'error');
	}

}

$(document).ready(async () => {

	$('.header-container').load('../nav-login.html');
	$('.footer-container').load('../footer.html');
	$('.ad-container').load('../ad.html');

	const userId = sessionStorage.getItem('userId');
	console.log('用戶 ID：', userId);
	if (!userId) {
		console.log('用戶 ID 未找到');
		alert('請重新登入');
		window.location.href = '/tinglinews/user/login.html';
		return;
	}
	fetchData(userId);

	$('#toggle-donated').on('click', () => {
		$('#saved-list').hide();
		$('#donated-list').toggle();
	});

	$('#toggle-saved').on('click', () => {
		$('#donated-list').hide();
		$('#saved-list').toggle();
	});

	// 更新個人資訊
	$('#profile-form').on('submit', handleSubmit);

	// 停止贊助
	$('#donated-table').on('click', '.stop-donate-btn', async (event) => {
		const id = $(event.target).data('id');
		console.log('按下停止贊助：' + id);

		const result = await Swal.fire({
			title: '確定要停止贊助嗎？',
			text: '停止後將無法恢復',
			icon: 'warning',
			showCancelButton: true,
			confirmButtonText: '確認停止',
			cancelButtonText: '取消'
		})

		if (!result.isConfirmed) {
			Swal.fire("並未停止", "", "info");
			return;
		}

		const url = `http://localhost:8080/tinglinews/donate/${id}`;
		const response = await fetch(url, { method: 'DELETE' });
		const { state, message, data } = await response.json();
		console.log(state, message, data);

		if (state) {
			Swal.fire('刪除成功', '', 'success');
		}
	});

	// 取消收藏
	$('#saved-table').on('click', '.cancel-saved-btn', async (event) => {
		const id = $(event.target).data('id');
		console.log('按下取消收藏：' + id);

		const result = await Swal.fire({
			title: '確定要取消收藏嗎？',
			text: '',
			icon: 'warning',
			showCancelButton: true,
			confirmButtonText: '確定',
			cancelButtonText: '取消'
		})

		if (!result.isConfirmed) {
			Swal.fire('未取消收藏', '', 'info');
			return;
		}

		const url = `http://localhost:8080/tinglinews/saved/${id}`;
		const response = await fetch(url, { method: 'DELETE' });
		const { state, message, data } = await response.json();
		console.log(state, message, data);

		if (state) {
			Swal.fire('取消收藏成功', '', 'success');
		}
	});
});