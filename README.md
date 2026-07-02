# simple-wallet - Wallet Management System

simple-wallet, Spring Boot ve PostgreSQL mimarisi üzerine kurulmuş, çoklu para birimi (Multi-Currency) desteğine ve canlı döviz kuru entegrasyonuna sahip kurumsal bir dijital cüzdan ve işlem (Transaction) yönetim sistemidir.

## 🛠️ Teknoloji Yığını ve Bağımlılıklar
* **Framework:** Java 17 & Spring Boot
* **Veritabanı & ORM:** PostgreSQL, Spring Data JPA & Hibernate
* **Mapping:** MapStruct (1.5.5.Final)
* **Kod Standartları:** Lombok & Java Records
* **Doğrulama & Güvenlik:** Spring Boot Starter Validation & Jakarta Persistence Lock

## 📌 Öne Çıkan Kurumsal Özellikler

### 1. Eşzamanlılık Koruması (Race Condition Prevention)
Aynı cüzdan üzerinde saliseler içerisinde birden fazla işlem (para çekme, transfer vb.) yapılmaya çalışıldığında bakiyenin tutarsızlaşmasını engellemek amacıyla **Pessimistic Write Lock (`@Lock(LockModeType.PESSIMISTIC_WRITE)`)** mekanizması entegre edilmiştir. İşlem bitene kadar ilgili satır veritabanı seviyesinde kilitlenir.

### 2. Canlı Döviz Kuru Entegrasyonu (Cross-Currency Transfer)
Sistem, `GlobalRestClient` altyapısı üzerinden Frankfurter API'sine bağlanarak anlık kur verilerini çeker. Farklı para birimlerine sahip cüzdanlar (TRY, USD, EUR) arasında transfer yapıldığında, kaynak hesaptan çıkan tutar baz alınarak hedef hesabın para birimine anlık kur üzerinden dinamik olarak dönüştürülüp eklenir.

### 3. Gelişmiş Hata ve Doğrulama Yönetimi (Global Exception Handling)
Uygulama genelinde fırlatılan tüm istisnalar (`WalletException`, `MethodArgumentNotValidException`) merkezi bir `RestControllerAdvice` tarafından yakalanır. İstemciye karmaşık sistem detayları sızdırılmadan, `code`, `message`, `details` ve `timestamp` alanlarını içeren temiz bir JSON şablonu (`ErrorMessage`) dönülür.

### 4. Veri İzlenebilirliği (Database Auditing)
`BaseEntity` soyut sınıfı altında `@CreatedDate` ve `@LastModifiedDate` anotasyonları kullanılarak, oluşturulan her cüzdanın ve kesilen her işlem makbuzunun veritabanına kayıt anı (`create_date`) ve son güncellenme anı (`update_date`) otomatik olarak izlenir.

## 🗄️ Mimari ve Katman Ayrımı

* **Entity Katmanında Class & Lombok:** Hibernate/JPA'in proxy üretebilmesi, boş constructor istemi ve mutable doğası gereği `Wallet` ve `Transaction` katmanında standart Java sınıfları ve Lombok tercih edilmiştir.
* **DTO Katmanında Java Record:** Verilerin ağ üzerinde taşınırken değiştirilemez (immutable) kalmasını garanti etmek adına istek ve yanıt nesneleri modern `record` yapısıyla kurgulanmıştır.
* **Gevşek Bağlılık (Loose Coupling):** MapStruct interface'leri `componentModel = "spring"` ile işaretlenerek servis katmanına `@RequiredArgsConstructor` üzerinden enjekte edilmiştir.

## 🔌 API Uç Noktaları (Endpoints)

### 1. Cüzdan Yönetimi (`WalletController`)
* **POST** `/api/v1/wallets` : Yeni bir cüzdan oluşturur. (İlk açılışta bakiye otomatik olarak `0.0` set edilir, mükerrer hesap numarası kontrolü yapılır).
* **GET** `/api/v1/wallets/{accountNumber}` : Hesap numarasına ait cüzdan detaylarını getirir.

### 2. İşlem Yönetimi (`TransactionController`)
* **POST** `/api/v1/transactions` : Para yatırma, para çekme ve hesaplar arası transfer işlemlerini tek bir kapıdan yönetir.

#### Örnek Transfer İstek Gövdesi (JSON):
```json
{
  "sourceAccountNumber": "ACC-200",
  "targetAccountNumber": "ACC-100",
  "amount": 17.92,
  "transactionType": "TRANSFER"
}
