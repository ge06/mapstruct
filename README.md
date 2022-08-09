# Map Struct

## Nedir?

İki farklı java nesnesini belirlenen ayarlara göre eşleştirmek ve dönüştürmek için kullanılan bir kod oluşturma aracıdır.

## Nasıl Çalışır?

MapStruct Java derleyicisine bağlı bir annotation işleyicisidir. Eşleştirmeleri derlenme zamanında üreterek yüksek performans ve geliştiriciler için kolay hata ayıklama ve geri bildirim sağlar. 

## Neden Kullanılır?

MapStruct DTO ve Entity dönüşümlerini boilerplate *(değişim az olduğu halde birçok yerde tekrarlı kullanılan)* bir yapıdan kurtarmak, get ve set işlemlerini azaltmak amacıyla kullanılır. MapStruct bu dönüşümleri kendi içerisinde daha hızlı yapmaktadır*.

*\*[MapStruct](https://mapstruct.org/) sitesindeki beyana göre.*

## Uygulama
Car Nesnesi
```
public class Car {
 
    private String make;
    private int numberOfSeats;
    private CarType type;
 
    //constructor, getters, setters etc.
}
```
CarDTO Nesnesi
```
public class CarDto {
 
    private String manufacturer;
    private int seatCount;
    private String type;
 
    //constructor, getters, setters etc.
}
```
CarMapper

Dönüşümün yapılacağı kısımdır.
```
@Mapper
public interface CarMapper {
  CarMapper INSTANCE = Mappers.getMapper( CarMapper.class );
  
  @Mapping(source = "make", target = "manufacturer")
  @Mapping(source = "numberOfSeats", target = "seatCount")
  CarDto carToCarDto(Car car);
 
  @Mapping(source = "name", target = "fullName")
  PersonDto personToPersonDto(Person person);
}
```
Car nesnesinden CarDto nesnesine bir dönüşüm yapılırken örneğin Car nesnesindeki make değişkeninin CarDto nesnesinde manufacturer olarak dönüşeceği `@Mapping` annotasyonu ile belirtilmiştir. İsim değişikliği olmayacak değişkenlerin belirtilmesine gerek yoktur. Otomatik olarak dönüşüm yapılacaktır. CarMapper sınıfı compile anında derlendiğinde şöyle bir kod bloğu oluşturacaktır:
```
// GENERATED CODE
public class CarMapperImpl implements CarMapper {
  @Override
  public CarDto carToCarDto(Car car) {
  if ( car == null ) {
  return null;
  }
  CarDto carDto = new CarDto();
  carDto.setManufacturer( car.getMake() );
  carDto.setSeatCount( car.getNumberOfSeats() );
  
  if ( car.getType() != null ) {
  carDto.setType( car.getType().name() );
  }
  
  return carDto;
  }
}
```

CarMapper dönüşümünü uygulama içerisinde kullanmak için şu şekilde çağırılmalıdır:

`CarDto carDto = CarMapper.INSTANCE.carToCarDto( car );`

## Mapper dönüşümü için diğer dönüşüm örnekleri ve soyutlaştırma

Aşağıdaki örnekte ToEntity adlı genel bir soyut sınıf oluşturulmuştur. Her bir aşamayı inceleyelim.
- `@Retention` annotasyonu bu annotasyonun nasıl saklanacağını belirtir. 3 tipi vardır. Bunlar: CLASS, RUNTIME ve SOURCE şeklindedir. CLASS olarak belirtildiğinde annotasyonun derlenme zamanında kaydedilmesi sağlanır. Böylece çalışma anında tekrar annotasyona gidip erişmesi gerekmez. MapStruct kütüphanesine ait bir özellik değildir. 
- id değeri ihmal edilmiş ve kullanılmamıştır.
- creationDate değeri bir metod ile oluşturulmuştur.
- groupName değeri önceki örnekler gibi name değerinden alınacaktır. Önceki örneklerde source başta target sonda olduğu unutulmamalıdır.

```
@Retention(RetentionPolicy.CLASS)
@Mapping(target = "id", ignore = true)
@Mapping(target = "creationDate", expression = "java(new java.util.Date())")
@Mapping(target = "name", source = "groupName")
public @interface ToEntity { }

```

Bu arayüzü örneğimizdeki mapper arayüzünde kullanımı şöyledir:
```
@Mapper
public interface CarMapper {
  CarMapper INSTANCE = Mappers.getMapper( CarMapper.class );
  @ToEntity
  @Mapping(source = "make", target = "manufacturer")
  @Mapping(source = "numberOfSeats", target = "seatCount")
  CarDto carToCarDto(Car car);
}
```

#### `@InheritInverseConfiguration` kullanımı
Bu annotasyon yapılan dönüşümün tam tersini otomatik olarak uygulamak için kullanılır. CarMapper adlı örneğimizdeki kullanımı şöyledir:
```
@Mapper
public interface CarMapper {
  CarMapper INSTANCE = Mappers.getMapper( CarMapper.class );
  
  @Mapping(source = "make", target = "manufacturer")
  @Mapping(source = "numberOfSeats", target = "seatCount")
  CarDto carToCarDto(Car car);
  
  @InheritInverseConfiguration
  Car carDtoToCar(CarDto carDto)
  
  @Mapping(source = "name", target = "fullName")
  PersonDto personToPersonDto(Person person);
}
```
#### MapStruct yapıcı metot kullanım tercihleri
MapStruct yapıcı metot kullanımını destekler. Ancak hangi yapıcı metodu kullanabileceği noktasında farklılıklar vardır.
```
public class Vehicle {
  protected Vehicle() { }
  // MapStruct will use this constructor, because it is a single public
constructor
  public Vehicle(String color) { }
}
public class Car {
  // MapStruct will use this constructor, because it is a parameterless empty
constructor
  public Car() { }
  public Car(String make, String color) { }
}
public class Truck {
  public Truck() { }
  // MapStruct will use this constructor, because it is annotated with @Default
  @Default
  public Truck(String make, String color) { }
}
public class Van {
  // There will be a compilation error when using this class because MapStruct
cannot pick a constructor
  public Van(String make) { }
  public Van(String make, String color) { }
}
```
### Avantaj ve Dezavantajları
#### Avantajlar
- Otomatik dönüşüm yaptığı için elle yapılan get set dönüşümlerinden oluşan hataları önler.
- Compile zamanında çalıştığı için olası hataların önceden farkedilmesi sağlanır.
- Nesne dönüşümünde soyutlama sağlar.
- Açık kaynaklı bir kütüphane. 
#### Dezavantajlar
- Mapper sınıflarının oluşturulması, soyutlandırmanın yapılandırılması ve dönüşümlerin uygulanması varolan bir proje için yüksek efor gerektirebilir. 
