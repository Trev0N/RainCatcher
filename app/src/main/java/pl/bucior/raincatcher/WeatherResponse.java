package pl.bucior.raincatcher;

import java.util.List;

public class WeatherResponse {

    private Double lat;
    private Double lon;
    private String timezone;
    private Integer timezone_offset;
    private WeatherDetail current;
    private List<WeatherDetail> hourly;

    public WeatherResponse() {
    }

    public WeatherResponse(Double lat, Double lon, String timezone, Integer timezone_offset, WeatherDetail current, List<WeatherDetail> hourly) {
        this.lat = lat;
        this.lon = lon;
        this.timezone = timezone;
        this.timezone_offset = timezone_offset;
        this.current = current;
        this.hourly = hourly;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getTimezone_offset() {
        return timezone_offset;
    }

    public void setTimezone_offset(Integer timezone_offset) {
        this.timezone_offset = timezone_offset;
    }

    public WeatherDetail getCurrent() {
        return current;
    }

    public void setCurrent(WeatherDetail current) {
        this.current = current;
    }

    public List<WeatherDetail> getHourly() {
        return hourly;
    }

    public void setHourly(List<WeatherDetail> hourly) {
        this.hourly = hourly;
    }

    public class WeatherDetail {
        private Long dt;
        private Long sunrise;
        private Long sunset;
        private Double temp;
        private Double feels_like;
        private Integer pressure;
        private Integer humidity;
        private Double dew_point;
        private Double uvi;
        private Integer clouds;
        private Integer visibility;
        private Double wind_speed;
        private Integer wind_deg;
        private List<WeatherDescription> weather;

        public WeatherDetail() {
        }

        public WeatherDetail(Long dt, Long sunrise, Long sunset, Double temp, Double feels_like, Integer pressure, Integer humidity, Double dew_point, Double uvi, Integer clouds, Integer visibility, Double wind_speed, Integer wind_deg, List<WeatherDescription> weather) {
            this.dt = dt;
            this.sunrise = sunrise;
            this.sunset = sunset;
            this.temp = temp;
            this.feels_like = feels_like;
            this.pressure = pressure;
            this.humidity = humidity;
            this.dew_point = dew_point;
            this.uvi = uvi;
            this.clouds = clouds;
            this.visibility = visibility;
            this.wind_speed = wind_speed;
            this.wind_deg = wind_deg;
            this.weather = weather;
        }

        public Long getDt() {
            return dt;
        }

        public void setDt(Long dt) {
            this.dt = dt;
        }

        public Long getSunrise() {
            return sunrise;
        }

        public void setSunrice(Long sunrise) {
            this.sunrise = sunrise;
        }

        public Long getSunset() {
            return sunset;
        }

        public void setSunset(Long sunset) {
            this.sunset = sunset;
        }

        public Double getTemp() {
            return temp;
        }

        public void setTemp(Double temp) {
            this.temp = temp;
        }

        public Double getFeels_like() {
            return feels_like;
        }

        public void setFeels_like(Double feels_like) {
            this.feels_like = feels_like;
        }

        public Integer getPressure() {
            return pressure;
        }

        public void setPressure(Integer pressure) {
            this.pressure = pressure;
        }

        public Integer getHumidity() {
            return humidity;
        }

        public void setHumidity(Integer humidity) {
            this.humidity = humidity;
        }

        public Double getDew_point() {
            return dew_point;
        }

        public void setDew_point(Double dew_point) {
            this.dew_point = dew_point;
        }

        public Double getUvi() {
            return uvi;
        }

        public void setUvi(Double uvi) {
            this.uvi = uvi;
        }

        public Integer getClouds() {
            return clouds;
        }

        public void setClouds(Integer clouds) {
            this.clouds = clouds;
        }

        public Integer getVisibility() {
            return visibility;
        }

        public void setVisibility(Integer visibility) {
            this.visibility = visibility;
        }

        public Double getWind_speed() {
            return wind_speed;
        }

        public void setWind_speed(Double wind_speed) {
            this.wind_speed = wind_speed;
        }

        public Integer getWind_deg() {
            return wind_deg;
        }

        public void setWind_deg(Integer wind_deg) {
            this.wind_deg = wind_deg;
        }

        public List<WeatherDescription> getWeather() {
            return weather;
        }

        public void setWeather(List<WeatherDescription> weather) {
            this.weather = weather;
        }
    }

    private class WeatherDescription {
        private Integer id;
        private String main;
        private String description;
        private String icon;

        public WeatherDescription() {
        }

        public WeatherDescription(Integer id, String main, String description, String icon) {
            this.id = id;
            this.main = main;
            this.description = description;
            this.icon = icon;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
