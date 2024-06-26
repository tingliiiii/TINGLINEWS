// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

// 從後端抓資料給 profile
const fetchData = async (userId) => {
	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/users/${userId}/profile`);
		const { state, message, data } = await response.json(); // 等待回應本文內容
		// console.log(state, message, data);

		if (!state) throw new Error(message);

		$('#userId').val(data.userId);
		$('#userEmail').val(data.userEmail);
		sessionStorage.setItem('userEmail', data.userEmail);
		$('#userName').val(data.userName);
		sessionStorage.setItem('userName', data.userName);
		$('#gender').val(data.gender);
		$('#birth').val(data.birthday);
		$('#phone').val(data.phone);

		renderFavorites(data.favoriteList);
        renderDonations(data.donationList);

	} catch (e) {
		console.error('資料讀取錯誤：' + e);
	}
}

// 收藏紀錄
const renderFavorites = (data) => {

	if(data.length === 0) {
		$('#saved-list-body').html('<tr><td colspan="5">尚無收藏紀錄</td></tr>');
		return;
	}
	const render = ({ favoriteId, news, favoriteTime }) => `
	<tr>
		<td>${favoriteId}</td>
		<td><a href="/tinglinews/news.html?id=${news.newsId}">
		${news.title}</a></td>
		<td>${news.publicTime}</td>
		<td>${favoriteTime}</td>
		<td>
			<button class="btn btn-close cancel-saved-btn" data-id="${favoriteId}"></button>
		</td>
	</tr>`;
	$('#saved-list-body').html(Array.isArray(data) ? data.map(render).join('') : render(data));
}

// 贊助紀錄
const renderDonations = (data) => {
	// // console.log(data);
	if(data.length === 0) {
		$('#donated-list-body').html('<tr><td colspan="7">尚無贊助紀錄</td></tr>');
		return;
	}
	const render = ({ donationId, frequency, amount, donatedTime, endTime, donateStatus }) => `
	<tr>
		<td>${donationId}</td>
		<td>${frequency}</td>
		<td>${amount}</td>
		<td>${donatedTime}</td>
		<td>${endTime === null ? 'N/A' : endTime}</td>
		<td>${donateStatus}</td>
		<td>
			<button class="btn btn-close stop-donate-btn" data-id="${donationId}"></button>
		</td>
	</tr>`;
	$('#donated-list-body').html(Array.isArray(data) ? data.map(render).join('') : render(data));
}

// 表單提交事件處理（更新個人資訊）
const handleSubmit = async (event) => {

	event.preventDefault();

	const result = await Swal.fire({
		title: '確定要更新嗎？',
		text: '',
		icon: 'info',
		showCancelButton: true,
		confirmButtonText: '更新',
		cancelButtonText: '取消'
	})

	if (!result.isConfirmed) {
		return;
	}

	const formData = {
		userId: $('#userId').val(),
		userEmail: $('#userEmail').val(),
		userName: $('#userName').val(),
		gender: $('#gender').val(),
		birthday: $('#birth').val(),
		phone: $('#phone').val()
	};
	await updateProfile(formData);
};

const updateProfile = async (formData) => {
	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/users/${formData.userId}/profile`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData)
		});

		const { state, message, data } = await response.json();
		// console.log(state, message, data);

		if (state) {
			sessionStorage.setItem('userName', formData.userName);
			sessionStorage.setItem('userEmail', formData.userEmail);
			Swal.fire(message, '', 'success');
		} else {
			Swal.fire(message, '', 'warning');
		}
	} catch (error) {
		console.error('更新過程發生錯誤：', error);
		Swal.fire('更新過程發生錯誤！請稍後再試', error, 'error');
	}

}

$(document).ready(async () => {

	$('.header-container').load('../nav-login.html');
	$('.footer-container').load('../footer.html');
	$('.ad-container').load('../ad.html');

	const data = JSON.parse(sessionStorage.getItem('userData'));
	// console.log('用戶 ID：', userId);
	if (!data) {
		// console.log('用戶 ID 未找到');
		Swal.fire('請重新登入', '', 'error');
		window.location.replace('/tinglinews/user/login.html');
		return;
	}
	const userId = data.userId;
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

		const donationId = $(event.target).data('id');
		console.log('按下停止贊助：' + donationId);

		const row = $(event.target).closest('tr');
		const status = row.find('td:nth-child(6)').text().trim();

		if (status === '已完成') {
			Swal.fire('贊助已完成', '', 'success');
			return;
		}

		const result = await Swal.fire({
			title: '確定要停止贊助嗎？',
			text: '停止後將無法恢復',
			icon: 'warning',
			showCancelButton: true,
			confirmButtonText: '確認停止',
			cancelButtonText: '取消'
		})

		if (!result.isConfirmed) {
			Swal.fire('贊助進行中', '', 'info');
			return;
		}

		try {
			const response = await fetch(`http://${ip}:8080/tinglinews/users/donations/${donationId}`, { method: 'DELETE' });
			const { state, message, data } = await response.json();
			// console.log(state, message, data);

			if (state) {
				Swal.fire('取消贊助成功', '', 'success');
				setTimeout(() => {
					window.location.reload();
				}, 1000);

			}
		} catch (e) {
			console.error('停止贊助時發生錯誤：', e);
			Swal.fire('停止過程發生錯誤！請稍後再試', e.message, 'error');
		}

	});

	// 取消收藏
	$('#saved-table').on('click', '.cancel-saved-btn', async (event) => {
		const favoriteId = $(event.target).data('id');
		// console.log('按下取消收藏：' + id);

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
		try {
			const response = await fetch(`http://${ip}:8080/tinglinews/users/favorites/${favoriteId}`, { method: 'DELETE' });
			const { state, message, data } = await response.json();
			// console.log(state, message, data);

			if (state) {
				$(event.target).closest('tr').remove();
				Swal.fire(message, '', 'success');
			}
		} catch (e) {
			console.error('取消收藏時發生錯誤：', e);
			Swal.fire('取消過程發生錯誤！請稍後再試', e.message, 'error');
		}

	});
});