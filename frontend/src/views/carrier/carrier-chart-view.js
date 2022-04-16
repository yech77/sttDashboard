import {PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-board/vaadin-board.js';
import '@vaadin/vaadin-board/vaadin-board-row.js';
import '@vaadin/vaadin-charts/vaadin-chart.js';
import '@vaadin/vaadin-grid/src/vaadin-grid.js';
import '../../../styles/shared-styles.js';
import '../../../styles/bakery-charts-theme.js';
import '../storefront/order-card.js';
import {html} from '@polymer/polymer/lib/utils/html-tag.js';

class CarrierChartView extends PolymerElement {
    static get template() {
        return html`
            <style include="shared-styles">
                :host {
                    width: 100%;
                    -webkit-overflow-scrolling: touch;
                    overflow: auto;
                }

                .vaadin-board-cell {
                    padding: var(--lumo-space-s);
                }

                *::-ms-backdrop,
                .vaadin-board-cell {
                    padding: 0;
                }

                .column-chart {
                    box-shadow: 0 2px 5px 0 rgba(23, 68, 128, 0.1);
                    border-radius: 4px;
                    height: calc(20vh - 64px) !important;
                    min-height: 200px;
                }

                .column-daily-chart {
                    box-shadow: 0 2px 5px 0 rgba(23, 68, 128, 0.1);
                    border-radius: 4px;
                    height: calc(20vh - 64px) !important;
                    min-height: 250px;
                }

                .pie-chart {
                    box-shadow: 0 2px 5px 0 rgba(23, 68, 128, 0.1);
                    border-radius: 4px;
                    height: calc(20vh - 64px) !important;
                    min-height: 250px;
                }

                #carrierDailyChart {
                    height: calc(30vh - 64px) !important;
                    min-height: 200px;
                }

                #monthlyProductSplit,
                #ordersGrid {
                    border-radius: 4px;
                    box-shadow: 0 2px 5px 0 rgba(23, 68, 128, 0.1);
                    height: calc(40vh - 64px) !important;
                    min-height: 355px;
                }

                vaadin-board-row.custom-board-row {
                    --vaadin-board-width-medium: 1440px;
                    --vaadin-board-width-small: 1024px;
                }

            </style>
            <vaadin-board>
                <vaadin-board-row>
                    <div id="divHeader" class="vaadin-board-cell"></div>
                </vaadin-board-row>
                <vaadin-board-row>
                    <div class="vaadin-board-cell">
                        <vaadin-chart id="deliveriesThisMonth" class="column-chart"></vaadin-chart>
                    </div>
                    <div class="vaadin-board-cell" board-cols="2">
                        <vaadin-chart id="smsThisDayChart" class="column-chart"></vaadin-chart>
                    </div>
                </vaadin-board-row>
                <vaadin-board-row>
                    <vaadin-chart id="carrierDailyChart" class="yearly-sales"></vaadin-chart>
                </vaadin-board-row>
                <vaadin-board-row>
                    <div class="vaadin-board-cell">
                        <vaadin-chart id="carrierTriPieChart" class="pie-chart"></vaadin-chart>
                    </div>
                    <div class="vaadin-board-cell">
                        <vaadin-chart id="carrierMonthlyPieChart" class="pie-chart"></vaadin-chart>
                    </div>
                    <div class="vaadin-board-cell">
                        <vaadin-chart id="carrierHourlyPieChart" class="pie-chart"></vaadin-chart>
                    </div>
                </vaadin-board-row>
            </vaadin-board>
        `;
    }

    static get is() {
        return 'carrier-chart-view';
    }

    // This method is overridden to measure the page load performance and can be safely removed
    // if there is no need for that.
    ready() {
        super.ready();
        this._chartsLoaded = new Promise((resolve, reject) => {
            // save the 'resolve' callback to trigger it later from the server
            this._chartsLoadedResolve = () => {
                resolve();
            };
        });

        this._gridLoaded = new Promise((resolve, reject) => {
            const listener = () => {
                if (!this.$['ordersGrid'].loading) {
                    this.$['ordersGrid'].removeEventListener('loading-changed', listener);
                    resolve();
                }
            };
            this.$['ordersGrid'].addEventListener('loading-changed', listener);
        });

        Promise.all([this._chartsLoaded, this._gridLoaded]).then(() => {
            window.performance.mark && window.performance.mark('bakery-page-loaded');
        });
    }
}

window.customElements.define(CarrierChartView.is, CarrierChartView);

