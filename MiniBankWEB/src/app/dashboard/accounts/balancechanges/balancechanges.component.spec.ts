import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BalancechangesComponent} from './balancechanges.component';

describe('BalancechangesComponent', () => {
    let component: BalancechangesComponent;
    let fixture: ComponentFixture<BalancechangesComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [BalancechangesComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(BalancechangesComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
