# Safe Transact - Wallet Management System

Safe Transact, Spring Boot ve PostgreSQL mimarisi üzerine kurulmuş, çoklu para birimi (Multi-Currency) desteğine ve canlı döviz kuru entegrasyonuna sahip kurumsal bir dijital cüzdan ve işlem (Transaction) yönetim sistemidir.

## 🛠️ Teknoloji Yığını ve Bağımlılıklar
* **Backend Framework:** Java 17 & Spring Boot
* **Veritabanı Katmanı:** PostgreSQL, Spring Data JPA & Hibernate
* **Nesne Eşleme (Mapping):** MapStruct (1.5.5.Final)
* **Kod Standartları:** Lombok & Java Records
* **Doğrulama & Güvenlik:** Spring Boot Starter Validation & Jakarta Persistence Lock

## 📌 Öne Çıkan Kurumsal Özellikler

### 1. Eşzamanlılık Koruması (Race Condition Prevention)
Aynı cüzdan üzerinde saliseler içerisinde birden fazla işlem (para çekme, transfer vb.) yapılmaya çalışıldığında bakiyenin eksiye düşmesini veya tutarsızlaşmasını engellemek amacıyla **Pessimistic Write Lock (`@Lock(LockModeType.PESSIMISTIC_WRITE)`)** mekanizması entegre edilmiştir. İşlem bitene kadar ilgili satır veritabanı seviyesinde kilitlenir.

### 2. Canlı Döviz Kuru Entegrasyonu (Cross-Currency Transfer)
Sistem, `GlobalRestClient` altyapısı üzerinden Frankfurter API'sine (`https://api.frankfurter.dev/v1`) bağlanarak anlık kur verilerini çeker. Farklı para birimlerine sahip cüzdanlar (TRY, USD, EUR) arasında transfer yapıldığında, kaynak hesaptan çıkan tutar baz alınarak hedef hesabın para birimine anlık kur üzerinden dönüştürülüp otomatik olarak eklenir.

### 3. Gelişmiş Hata ve Doğrulama Yönetimi (Global Exception Handling)
Uygulama genelinde fırlatılan tüm istisnalar (`WalletException`, `MethodArgumentNotValidException`, `Exception`) merkezi bir `RestControllerAdvice` tarafından yakalanır. İstemciye (Postman/Frontend) karmaşık sistem detayları (Stack Trace) sızdırılmadan, `code`, `message`, `details` ve `timestamp` alanlarını içeren temiz ve standart bir JSON şablonu (`ErrorMessage`) dönülür.

### 4. Veri İzlenebilirliği (Database Auditing)
`BaseEntity` sınıfı altında `@CreatedDate` ve `@LastModifiedDate` anotasyonları kullanılarak, oluşturulan her cüzdanın ve kesilen her işlem makbuzunun veritabanına kayıt anı (`create_date`) ve son güncellenme anı (`update_date`) otomatik olarak izlenir.

## 🗄️ Veritabanı ve Nesne Modeli (Domain Architecture)

* **Wallets (Cüzdanlar):** `account_number` (Unique), `balance` (Kuruş hassasiyeti için `BigDecimal`), ve `currency_type` (Enum: TRY, USD, EUR) alanlarını barındırır.
* **Transactions (İşlemler):** Cüzdan ile `ManyToOne (FetchType.LAZY)` ilişkili olup, yapılan her `DEPOSIT` (Para Yatırma), `WITHDRAW` (Para Çekme) ve `TRANSFER` (Havale/EFT) hareketini tutar, çift taraflı muhasebe log kaydı (makbuz) oluşturur.

## 🔌 API Uç Noktaları (Endpoints)

### 1. Cüzdan Yönetimi (`WalletController`)
* **POST** `/api/v1/wallets` : Yeni bir cüzdan oluşturur. (İlk açılışta bakiye otomatik olarak `0.0` set edilir, mükerrer hesap numarası kontrolü yapılır).
* **GET** `/api/v1/wallets/{accountNumber}` : Hesap numarasına ait cüzdan detaylarını getirir.

### 2. İşlem Yönetimi (`TransactionController`)
* **POST** `/api/v1/transactions` : Para yatırma, para çekme ve hesaplar arası transfer işlemlerini tek bir kapıdan yönetir.

#### Örnek Transfer İstek Gövdesi (JSON):
```json
{
  "sourceAccountNumber": "ACC-100",
  "targetAccountNumber": "ACC-200",
  "amount": 5000.00,
  "transactionType": "TRANSFER"
}
