// const ip = '127.0.0.1';
const ip = 'localhost';


// 從後端抓資料
const fetchData = async (id) => {
	const url = `http://${ip}:8080/tinglinews/news/${id}`;
	try {
		const response = await fetch(url);
		const { state, message, data } = await response.json();
		// console.log(state, message, data);
		renderData(data);
	} catch (e) {
		console.error(e);
	}
};

const renderData = (data) => {

	// Base64 字串轉圖片
	if (data.image) {
		// 檢查圖片格式並動態設置
		let imageFormat = 'jpeg'; // 默認為 jpeg
		if (data.image.startsWith('/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAoHBwkHBgoICAgLCg8LDhgQDg0NDh0VFhEYITIjJh0pKycyMTI0GyUoKDcwJzgsLCkqLjYxNTU1HyY3Pi0zP')) {
			imageFormat = 'png';
		}
		data.image = 'data:image/' + imageFormat + ';base64,' + data.image;
	}

	const newsItem = (item) => {
		const journalists = item.journalists.map(journalist => journalist.userName).join(' ');
		return `
			<h1 class="news-title">${item.title}</h1>
			<div class="news-content">           
				<p class="news-date">
					<a href="/tinglinews/list.html?id=${item.tag.tagId}" class="btn btn-outline-secondary btn-sm me-2">${item.tag.tagName}</a>
					<small>記者 ${journalists}&emsp;發布時間 ${item.publicTime}&emsp;更新時間 ${item.updatedTime}</small>
				</p>
				<figure class="news-img-container">
					<img src="${item.image}" class="news-img">
				</figure>
				<div class="news-paragraph">${item.content}</div>
			</div>
 		`;
	};
	/*
					<form method="POST" id="saved-form" class="d-inline-block">
					<input type="hidden" name="newsId" value="${item.newsId}">
					<button type="submit" class="btn btn-secondary btn-sm">收藏</button>
				</form>
	*/
	$('.news-container').html(Array.isArray(data) ? data.map(newsItem).join('') : newsItem(data));

}

const handleSubmit = async (event) => {
	event.preventDefault();

	if (sessionStorage.getItem('userId') == null) {
		Swal.fire('登入解鎖收藏功能', '', 'info');
		return;
	}

	const formData = {
		userId: sessionStorage.getItem('userId'),
		newsId: $('#newsId').val()
	};

	try {
		const response = await fetch('/tinglinews/user/saved', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(formData)
		});
		const { state, message, data } = await response.json();
		// console.log(state, message, data);
		if (state) {
			Swal.fire(message, '可至個人資料頁面查看收藏紀錄', 'success');
		} else {
			Swal.fire(message, '', 'warning');
		}
	} catch (error) {
		console.error('收藏發生錯誤：', error);
		Swal.fire('收藏發生錯誤 請稍後再試', error, 'error');
	}
}

// 待 DOM 加載完成之後再執行
$(document).ready(() => {
	if (sessionStorage.getItem('userId') != null) {
		$('.header-container').load('nav-login.html');
		$('#saved-btn').text('收藏這篇文章！');
	} else {
		$('.header-container').load('nav.html');
		$('#saved-btn').text('登入收藏這篇文章！');
	}
	$('.footer-container').load('footer.html');
	$('.ad-container').load('ad.html');

	// 把 uri 抓下來渲染頁面
	const queryString = window.location.search;
	// console.log(`QueryString: ${queryString}`);
	const urlParams = new URLSearchParams(queryString);
	// console.log(`URLParams: ${urlParams}`);
	const id = urlParams.get('id');
	// console.log(`ID 參數: ${id}`);
	fetchData(id);

	// 設定收藏按鈕的隱藏欄位
	$('#newsId').val(id);

	$('#saved-form').on('submit', handleSubmit);

});
