package com.gwtplatform.carstore.client.application;

import java.util.ArrayList;

import org.jukito.JukitoRunner;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.gwtplatform.carstore.client.application.cars.CarsPresenter;
import com.gwtplatform.carstore.client.application.cars.car.CarPresenter;
import com.gwtplatform.carstore.client.application.cars.car.CarProxyFactory;
import com.gwtplatform.carstore.client.application.testutils.CarServiceImpl;
import com.gwtplatform.carstore.client.application.testutils.PresenterTestModule;
import com.gwtplatform.carstore.client.application.testutils.PresenterWidgetTestBase;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.rest.CarService;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.NoResult;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JukitoRunner.class)
public class CarsPresenterTest extends PresenterWidgetTestBase {
    public static class Module extends PresenterTestModule {
        @Override
        protected void configurePresenterTest() {
            forceMock(CarProxyFactory.class);
            bind(CarService.class).to(CarServiceImpl.class).in(TestSingleton.class);
        }
    }

    @Inject
    CarsPresenter carsPresenter;
    @Inject
    CarsPresenter.MyView view;
    @Inject
    CarProxyFactory carProxyFactory;
    @Inject
    CarPresenter.MyProxy proxy;

    @Test
    public void onEditCar(PlaceManager placeManager, ManufacturerDto manufacturerDto) {
        // Given
        CarDto carDto = mock(CarDto.class);
        when(carDto.getManufacturer()).thenReturn(manufacturerDto);
        when(carProxyFactory.create(carDto, carDto.getManufacturer().getName() + carDto.getModel())).thenReturn(proxy);
        when(proxy.getNameToken()).thenReturn("token");

        PlaceRequest placeRequest = new PlaceRequest("token");

        // When
        carsPresenter.onEdit(carDto);

        // Then
        verify(placeManager).revealPlace(eq(placeRequest));
    }

    @Test
    public void onCreate(PlaceManager placeManager) {
        // Given
        PlaceRequest placeRequest = new PlaceRequest(NameTokens.newCar);

        // When
        carsPresenter.onCreate();

        // Then
        verify(placeManager).revealPlace(eq(placeRequest));
    }

    @Test
    public void onDelete(CarDto carDto, HasData<CarDto> hasCarData, Range range) {
        // Given we have DeleteCarAction
        dispatcher.given(new TypeLiteral<Action<NoResult>>() {
        }).willReturn(new NoResult());

        // And GetCarAction for fetching after delete
        GetResults<CarDto> result = new GetResults<CarDto>(new ArrayList<CarDto>());
        dispatcher.given(new TypeLiteral<Action<GetResults<CarDto>>>() {
        }).willReturn(result);

        // And display is setup
        when(view.getCarDisplay()).thenReturn(hasCarData);

        // And range is setup
        HasData<CarDto> display = view.getCarDisplay();
        when(display.getVisibleRange()).thenReturn(range);

        // When
        carsPresenter.onDelete(carDto);

        // Then
        verify(view).setCarsCount(-1);
    }

    @Test
    public void onFetchData(ArrayList<CarDto> carDtos) {
        // Given
        GetResults<CarDto> result = new GetResults<CarDto>(new ArrayList<CarDto>());
        dispatcher.given(new TypeLiteral<Action<GetResults<CarDto>>>() {
        }).willReturn(result);

        // When
        carsPresenter.fetchData(0, 1);

        // Then
        verify(view).displayCars(0, carDtos);
    }

    @Test
    public void onFetchDataThreeCars(ArrayList<CarDto> carDtos) {
        // Given
        GetResults<CarDto> result = new GetResults<CarDto>(carDtos);
        dispatcher.given(new TypeLiteral<Action<GetResults<CarDto>>>() {
        }).willReturn(result);

        // When
        carsPresenter.fetchData(0, 3);

        // Then
        verify(view).displayCars(0, carDtos);
    }
}
