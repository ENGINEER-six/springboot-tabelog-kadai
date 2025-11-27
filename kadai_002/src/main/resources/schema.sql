/* ロール  */
CREATE TABLE IF NOT EXISTS roles(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* 【主キー】ID */
	name VARCHAR(50) NOT NULL								/* ロール名 */
);

/* ユーザー */
CREATE TABLE IF NOT EXISTS users(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, /* ID【主キー】【自動採番】 */
	role_id INT NOT NULL,												/* ロールID【外部キー】 */
	name VARCHAR(50) NOT NULL,								/* 氏名 */
	furigana VARCHAR(50) NOT NULL,							/* フリガナ */
	postal_code VARCHAR(50) NOT NULL,						/* 郵便番号 */
	address VARCHAR(255) NOT NULL,							/* 住所 */
	phone_number VARCHAR(50) NOT NULL,					/* 電話番号 */
	birthday Date,															/* 誕生日 */
	occupation VARCHAR(50),											/* 職業 */
	email VARCHAR(255) NOT NULL,								/* メールアドレス【重複禁止】 */
	password VARCHAR(255) NOT NULL,						/* パスワード */
	enabled BOOLEAN NOT NULL,									/* ユーザーが有効かどうか */
	stripe_customer_id VARCHAR(255),							/* 決済サービスのお客様ID */
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,															/* 作成日時 */
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,	/* 更新日時 */

	/* 重複禁止 */
	UNIQUE(email),
	UNIQUE(stripe_customer_id),
	
	/* 外部キーが主キーを参照する  */
	FOREIGN KEY (role_id) REFERENCES roles(id)
);

/* メール認証 */
CREATE TABLE IF NOT EXISTS verification_tokens(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,		/* ID【主キー】【自動採番】 */
	user_id INT NOT NULL,													/* ユーザーID【外部キー】*/
	token VARCHAR(255) NOT NULL,									/* トークン（ランダムな文字列） */
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,															/* 作成日時 */
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,	/* 更新日時 */
	
	/* 重複禁止 */
	UNIQUE(user_id),
	/* 外部キーが主キーを参照する */
	FOREIGN KEY(user_id) REFERENCES users(id)
);

/* 店舗 */
CREATE TABLE IF NOT EXISTS restaurants(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* ID【主キー】【自動採番】 */
	name VARCHAR(50) NOT NULL,								/* 店舗名 */
	image VARCHAR(255),												/* 店舗画像 */
	description TEXT NOT NULL,										/* 店舗説明 */
	lowest_price INT NOT NULL,										/* 最低価格 */
	highest_price INT NOT NULL,									/* 最高価格 */
	postal_code VARCHAR(50) NOT NULL,						/* 郵便番号 */
	address VARCHAR(255) NOT NULL,							/* 住所 */
	opening_time TIME NOT NULL,									/* 開店時間 */
	closing_time TIME NOT NULL,									/* 閉店時間 */
	seating_capacity INT NOT NULL,								/* 座席数 */
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,															/* 作成日時 */
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP	/* 更新日時 */
);

/* カテゴリ */
CREATE TABLE IF NOT EXISTS categories(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* ID【主キー】【自動採番】 */
	name VARCHAR(50) NOT NULL								/* カテゴリ名 */
);

/* 店舗-カテゴリ【中間テーブル】 */
CREATE TABLE IF NOT EXISTS category_restaurant(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* ID【主キー】【自動採番】 */
	restaurant_id INT NOT NULL,									/* 店舗ID【外部キー】 */
	category_id INT NOT NULL,										/* カテゴリID【外部キー】 */
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,															/* 作成日時 */
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,	/* 更新日時 */
	
	/* 重複禁止 */
	UNIQUE(restaurant_id, category_id),
	/* 外部キーが主キーを参照する */
	FOREIGN KEY(restaurant_id) REFERENCES restaurants(id),
	FOREIGN KEY(category_id) REFERENCES categories(id)
);

/*  定休日 */
CREATE TABLE IF NOT EXISTS regular_holidays(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* ID【主キー】【自動採番】 */
	day VARCHAR(50) NOT NULL,									/*  定休日 */
	day_index INT															/* 定休日の番号（予約時のカレンダに定休日を設定するときに使う） */
);

/* 店舗-定休日【中間テーブル】  */
CREATE TABLE IF NOT EXISTS regular_holiday_restaurant(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* ID【主キー】【自動採番】 */
	restaurant_id INT NOT NULL,									/* 店舗ID */
	regular_holiday_id INT NOT NULL,								/* 定休日ID */
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,															/* 作成日時 */
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,	/* 更新日時 */
	
	/* 重複禁止 */
	UNIQUE(restaurant_id, regular_holiday_id),
	/* 外部キーが主キーを参照する */
	FOREIGN KEY(restaurant_id) REFERENCES restaurants(id),
	FOREIGN KEY(regular_holiday_id) REFERENCES regular_holidays(id)
);

/* 会社概要 */
CREATE TABLE IF NOT EXISTS companies(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* ID【主キー】【自動採番】 */
	name VARCHAR(50) NOT NULL,								/* 会社名 */
	postal_code VARCHAR(50) NOT NULL,						/* 郵便番号 */
	address VARCHAR(255) NOT NULL,							/* 所在地 */
	representative VARCHAR(50) NOT NULL,					/* 代表者 */
	establishment_date VARCHAR(50) NOT NULL,			/* 設立年月日 */
	capital VARCHAR(50) NOT NULL,								/* 資本金 */
	business VARCHAR(255) NOT NULL,							/* 事業内容 */
	number_of_employees VARCHAR(50) NOT NULL,		/* 従業員 */
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,															/* 作成日時 */
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP	/* 更新日時 */	
);

/* 利用規約 */
CREATE TABLE IF NOT EXISTS terms(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* ID【主キー】【自動採番】 */
	content TEXT NOT NULL,											/* 利用規約の本文 */
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,															/* 作成日時 */
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP	/* 更新日時 */	
);

/* レビュー */
CREATE TABLE IF NOT EXISTS reviews(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* ID【主キー】【自動採番】 */
	content TEXT NOT NULL,											/* レビュー内容 */
	score INT NOT NULL,												/* スコア（星の数） */
	restaurant_id INT NOT NULL,									/* 店舗のID */
	user_id INT NOT NULL,												/* ユーザーのID */
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,															/* 作成日時 */
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,	/* 更新日時 */	
	
	/* 重複禁止 */
	UNIQUE(restaurant_id, user_id),
	/* 外部キーが主キーを参照する */
	FOREIGN KEY(restaurant_id) REFERENCES restaurants(id),
	FOREIGN KEY (user_id) REFERENCES users(id)
);

/* 予約 */
CREATE TABLE IF NOT EXISTS reservations(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* ID【主キー】【自動採番】 */
	reserved_datetime DATETIME NOT NULL,					/* 予約日時 */
	number_of_people INT NOT NULL,								/* 予約人数 */
	restaurant_id INT NOT NULL,									/* 店舗のid */
	user_id INT NOT NULL,												/* ユーザーのID */
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,															/* 作成日時 */
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,	/* 更新日時 */	
	
	/* 外部キーが主キーを参照する */
	FOREIGN KEY(restaurant_id) REFERENCES restaurants(id),
	FOREIGN KEY (user_id) REFERENCES users(id)
);

/* お気に入り */
CREATE TABLE IF NOT EXISTS favorites(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,	/* ID【主キー】【自動採番】 */
	restaurant_id INT NOT NULL,									/* 店舗のid */
	user_id INT NOT NULL,												/* ユーザーのID */
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,															/* 作成日時 */
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,	/* 更新日時 */	
	
	/* 重複禁止 */
	UNIQUE(restaurant_id, user_id),
	/* 外部キーが主キーを参照する */
	FOREIGN KEY(restaurant_id) REFERENCES restaurants(id),
	FOREIGN KEY (user_id) REFERENCES users(id)
);